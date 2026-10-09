param(
    [Parameter(Mandatory = $true)]
    [string]$Image,
    [ValidateRange(1, 60)]
    [int]$HealthAttempts = 30
)

$ErrorActionPreference = 'Stop'
$docker = Join-Path $env:ProgramFiles 'Docker/Docker/resources/bin/docker.exe'
$name = 'licence-ci-app'
$backup = 'licence-ci-previous'
$url = 'http://127.0.0.1:8085/business-licence-renewal/health'

function Invoke-Docker {
    $ErrorActionPreference = 'Continue'
    $output = & $docker @args 2>&1
    $code = $LASTEXITCODE
    if ($code -ne 0) {
        throw "Docker command failed: $args`n$output"
    }
    $output
}

function Wait-ForHealth {
    param([int]$Attempts)
    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        try {
            $health = Invoke-RestMethod -Uri $url -TimeoutSec 3
            if ($health.status -eq 'UP') {
                return $true
            }
        } catch {
            Write-Host "Waiting for application: $attempt"
        }
        Start-Sleep -Seconds 2
    }
    return $false
}

# Check the image before stopping the working application.
Invoke-Docker image inspect $Image | Out-Null
$containers = @(Invoke-Docker ps -a --format '{{.Names}}')
if ($containers -contains $backup) {
    throw "Previous backup exists: $backup. Check it before deploying."
}

$hadPrevious = $containers -contains $name
$stopAttempted = $false
$movedPrevious = $false
$runAttempted = $false

try {
    if ($hadPrevious) {
        $stopAttempted = $true
        Invoke-Docker stop $name | Out-Null
        Invoke-Docker rename $name $backup | Out-Null
        $movedPrevious = $true
    }

    $runAttempted = $true
    Invoke-Docker run -d --name $name `
        --restart unless-stopped `
        -p 127.0.0.1:8085:8080 `
        --mount "source=licence-ci-data,target=/var/lib/licence" `
        $Image | Out-Null

    if (-not (Wait-ForHealth -Attempts $HealthAttempts)) {
        throw 'Docker application health check failed.'
    }
} catch {
    $failure = $_
    Write-Host "Deployment failed: $($failure.Exception.Message)"

    try {
        if ($runAttempted) {
            $current = @(Invoke-Docker ps -a --format '{{.Names}}')
            if ($current -contains $name) {
                try {
                    Invoke-Docker logs --tail 30 $name |
                        ForEach-Object { Write-Host "$_" }
                } catch {
                    Write-Host 'Could not read candidate logs.'
                }
                Invoke-Docker rm -f $name | Out-Null
            }
        }

        if ($movedPrevious) {
            Invoke-Docker rename $backup $name | Out-Null
        }

        if ($stopAttempted) {
            Invoke-Docker start $name | Out-Null
            if (-not (Wait-ForHealth -Attempts 30)) {
                throw 'Restored container did not pass its health check.'
            }
            Write-Host 'ROLLBACK PASSED: previous application restored and healthy.'
        }
    } catch {
        throw "Recovery needs attention: $($_.Exception.Message). Original failure: $($failure.Exception.Message)"
    }

    throw $failure
}

# Cleanup happens only after the new application passes health checks.
if ($movedPrevious) {
    Invoke-Docker rm $backup | Out-Null
}

Write-Host "Docker deployment passed: $Image"
Write-Host $url
