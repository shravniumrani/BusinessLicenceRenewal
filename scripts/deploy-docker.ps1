param(
    [Parameter(Mandatory = $true)]
    [string]$Image
)

$ErrorActionPreference = 'Stop'
$docker = Join-Path $env:ProgramFiles 'Docker/Docker/resources/bin/docker.exe'
$name = 'licence-ci-app'
$backup = 'licence-ci-previous'
$url = 'http://127.0.0.1:8085/business-licence-renewal/health'

function Invoke-Docker {
    & $docker @args
    if ($LASTEXITCODE -ne 0) {
        throw "Docker command failed: $args"
    }
}

$containers = @(Invoke-Docker ps -a --format '{{.Names}}')
if ($containers -contains $backup) {
    throw "Previous backup exists: $backup. Check it before deploying."
}

$hadPrevious = $containers -contains $name
$created = $false

try {
    Invoke-Docker image inspect $Image | Out-Null

    if ($hadPrevious) {
        Invoke-Docker stop $name | Out-Null
        Invoke-Docker rename $name $backup
    }

    Invoke-Docker run -d --name $name `
        --restart unless-stopped `
        -p 127.0.0.1:8085:8080 `
        --mount "source=licence-ci-data,target=/var/lib/licence" `
        $Image | Out-Null
    $created = $true

    $ready = $false
    for ($attempt = 1; $attempt -le 30; $attempt++) {
        try {
            $health = Invoke-RestMethod -Uri $url -TimeoutSec 3
            if ($health.status -eq 'UP') {
                $ready = $true
                break
            }
        } catch {
            Write-Host "Waiting for Docker application: $attempt"
        }
        Start-Sleep -Seconds 2
    }

    if (-not $ready) {
        throw 'Docker application health check failed.'
    }

    if ($hadPrevious) {
        Invoke-Docker rm $backup | Out-Null
    }

    Write-Host "Docker deployment passed: $Image"
    Write-Host $url
} catch {
    $failure = $_
    if ($created) {
        & $docker logs --tail 60 $name
        Invoke-Docker rm -f $name | Out-Null
    }
    if ($hadPrevious) {
        Invoke-Docker rename $backup $name
        Invoke-Docker start $name | Out-Null
        Write-Host 'Previous container restored.'
    }
    throw $failure
}

