param([string]$WarPath = '')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
if (-not $WarPath) { $WarPath = Join-Path $projectRoot 'target\business-licence-renewal.war' }
if (-not (Test-Path -LiteralPath $WarPath -PathType Leaf)) {
    throw 'WAR missing. Run .\mvnw.cmd -B clean verify first, or pass -WarPath with the Jenkins WAR path.'
}
$WarPath = (Resolve-Path -LiteralPath $WarPath).Path
$docker = Join-Path $env:ProgramFiles 'Docker/Docker/resources/bin/docker.exe'
$id = [Guid]::NewGuid().ToString('N').Substring(0,12)
$name = "licence-ansible-test-$id"
$payload = Join-Path ([IO.Path]::GetTempPath()) "licence-ansible-$id"
$logs = Join-Path $projectRoot "target\ansible-evidence\$id"
New-Item -ItemType Directory -Path $payload,$logs -Force | Out-Null
$report = Join-Path $logs 'results.md'
$script:records = New-Object System.Collections.Generic.List[string]
$script:records.Add("# Ansible actual execution evidence`nStarted: $(Get-Date -Format o)`nTarget: $name`nMode: isolated Linux container, local Ansible connection")
$passed = $false
function Invoke-RecordedDocker {
    param([string]$Label,[string[]]$DockerArgs,[switch]$AllowFailure)
    $ErrorActionPreference = 'Continue'
    $lines = & $docker @DockerArgs 2>&1
    $code = $LASTEXITCODE
    $body = ($lines | ForEach-Object { "$_" }) -join "`n"
    $body | Set-Content -LiteralPath (Join-Path $logs "$Label.log") -Encoding UTF8
    Write-Host $body
    $script:records.Add("## $Label`nExit code: $code`n~~~text`n$body`n~~~")
    if ($code -ne 0 -and -not $AllowFailure) { throw "$Label failed with exit code $code. See $logs" }
    return [PSCustomObject]@{ Code=$code; Text=$body }
}
try {
    Copy-Item -LiteralPath $WarPath -Destination (Join-Path $payload 'deploy.war')
    $baseline = (Get-FileHash -LiteralPath $WarPath -Algorithm SHA256).Hash.ToLowerInvariant()
    $script:records.Add("Baseline WAR SHA256: $baseline")
    Invoke-RecordedDocker -Label 'build' -DockerArgs @('build','-t','licence-ansible-verified:1',$PSScriptRoot) | Out-Null
    Invoke-RecordedDocker -Label 'create' -DockerArgs @('run','--init','-d','--name',$name,'--label','licence.ansible.demo=true','--mount',"type=bind,source=$PSScriptRoot,target=/ansible,readonly",'--mount',"type=bind,source=$payload,target=/payload,readonly",'licence-ansible-verified:1') | Out-Null
    $play = @('exec',$name,'ansible-playbook','-i','inventory.ini','playbook.yml')
    Invoke-RecordedDocker -Label 'provision' -DockerArgs $play | Out-Null
    $second = Invoke-RecordedDocker -Label 'idempotency' -DockerArgs $play
    if ($second.Text -notmatch '(?m)^target-node\s*:\s*ok=\d+\s+changed=0\s+unreachable=0\s+failed=0') {
        throw 'Second run did not prove changed=0 and failed=0.'
    }
    # Deliberately invalid candidate, only inside this disposable test target.
    [IO.File]::WriteAllText((Join-Path $payload 'deploy.war'),'INTENTIONALLY INVALID WAR - ROLLBACK TEST')
    $recovery = Invoke-RecordedDocker -Label 'rejected-candidate-and-recovery' -DockerArgs $play -AllowFailure
    if ($recovery.Code -ne 2 -or $recovery.Text -notmatch 'ROLLBACK VERIFIED' -or $recovery.Text -notmatch 'CANDIDATE REJECTED') {
        throw 'Expected failed deployment plus verified recovery was not observed.'
    }
    $hash = Invoke-RecordedDocker -Label 'restored-hash' -DockerArgs @('exec',$name,'sha256sum','/usr/local/tomcat/webapps/business-licence-renewal.war')
    if (-not $hash.Text.Trim().StartsWith($baseline)) { throw 'Restored WAR differs from baseline.' }
    $health = Invoke-RecordedDocker -Label 'restored-health' -DockerArgs @('exec',$name,'curl','--fail','--silent','--show-error','http://127.0.0.1:8080/business-licence-renewal/health')
    if (($health.Text | ConvertFrom-Json).status -ne 'UP') { throw 'Restored application is not UP.' }
    Copy-Item -LiteralPath $WarPath -Destination (Join-Path $payload 'deploy.war') -Force
    $after = Invoke-RecordedDocker -Label 'post-recovery-idempotency' -DockerArgs $play
    if ($after.Text -notmatch '(?m)^target-node\s*:\s*ok=\d+\s+changed=0\s+unreachable=0\s+failed=0') {
        throw 'Post-recovery run was not idempotent.'
    }
    $passed = $true
} finally {
    $script:records.Add("## Suite outcome`nPassed: $passed`nA failed candidate is intentional; overall success requires actual restoration checks.")
    [IO.File]::WriteAllText($report,($script:records -join "`n`n"),(New-Object Text.UTF8Encoding($false)))
    # Remove only the uniquely named test target; existing application and volumes are untouched.
    $ErrorActionPreference = 'Continue'
    & $docker cp "${name}:/usr/local/tomcat/logs" (Join-Path $logs 'tomcat-logs') 2>&1 | Out-File (Join-Path $logs 'capture-logs.log') -Encoding UTF8
    & $docker rm -f $name 2>&1 | Out-File (Join-Path $logs 'cleanup.log') -Encoding UTF8
    Remove-Item -LiteralPath $payload -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "Actual evidence: $report"
}
if (-not $passed) { throw 'Ansible suite failed.' }
Write-Host 'ANSIBLE SUITE PASSED: real health, idempotency, rejected candidate, exact WAR restoration, healthy recovery.'
