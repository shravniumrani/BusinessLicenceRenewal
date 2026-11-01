param(
    [string]$TomcatHome = "C:\Users\SHRAVNI\Documents\apache-tomcat-10.1.60",
    [string]$JavaHome = "C:\Program Files\Java\jdk-21"
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Set-Location $projectRoot

$java = Join-Path $JavaHome 'bin\java.exe'
$war = Join-Path $projectRoot 'target\business-licence-renewal.war'
$maven = Join-Path $projectRoot 'mvnw.cmd'

if (-not (Test-Path $java)) {
    throw "Java not found: $java"
}
if (-not (Test-Path "$TomcatHome\conf\server.xml")) {
    throw "Tomcat not found: $TomcatHome"
}
if (-not (Test-Path $war)) {
    throw 'Build the WAR before running this script.'
}

# Check that the test port is available.
$listener = New-Object System.Net.Sockets.TcpListener(
    [System.Net.IPAddress]::Loopback, 8082
)
try {
    $listener.Start()
} finally {
    $listener.Stop()
}

# Give this test run its own server folders and database.
$runId = [Guid]::NewGuid().ToString('N')
$base = Join-Path $projectRoot "target\selenium-tomcat-$runId"

foreach ($folder in @('conf', 'logs', 'temp', 'webapps', 'work', 'data')) {
    New-Item -ItemType Directory -Force "$base\$folder" | Out-Null
}

Copy-Item "$TomcatHome\conf\*" "$base\conf" -Recurse -Force
Copy-Item $war "$base\webapps\business-licence-renewal.war"

# Configure the separate server to use port 8082.
$configPath = Join-Path $base 'conf\server.xml'
[xml]$config = Get-Content $configPath -Raw
$config.Server.SetAttribute('port', '-1')

$connectors = @($config.SelectNodes('/Server/Service/Connector'))
$httpConnector = $connectors |
    Where-Object { $_.GetAttribute('port') -eq '8080' } |
    Select-Object -First 1

if ($null -eq $httpConnector) {
    throw 'Cannot find the Tomcat HTTP connector.'
}

foreach ($connector in $connectors) {
    if ($connector -ne $httpConnector) {
        [void]$connector.ParentNode.RemoveChild($connector)
    }
}

$httpConnector.SetAttribute('port', '8082')
$httpConnector.SetAttribute('address', '127.0.0.1')
$config.Save($configPath)

$classPath = "$TomcatHome\bin\bootstrap.jar;$TomcatHome\bin\tomcat-juli.jar"
$javaArguments = @(
    ('"-Dcatalina.home={0}"' -f $TomcatHome)
    ('"-Dcatalina.base={0}"' -f $base)
    ('"-Djava.io.tmpdir={0}\temp"' -f $base)
    ('"-Dlicence.data.dir={0}\data"' -f $base)
    ('"-Djava.util.logging.config.file={0}\conf\logging.properties"' -f $base)
    '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager'
    '-classpath'
    ('"{0}"' -f $classPath)
    'org.apache.catalina.startup.Bootstrap'
    'start'
)

$server = $null
$exitCode = 1
$baseUrl = 'http://127.0.0.1:8082/business-licence-renewal'

try {
    $server = Start-Process -NoNewWindow -FilePath $java `
        -ArgumentList $javaArguments `
        -RedirectStandardOutput "$base\logs\stdout.log" `
        -RedirectStandardError "$base\logs\stderr.log" `
        -PassThru

    $ready = $false

    for ($attempt = 1; $attempt -le 60; $attempt++) {
        $server.Refresh()
        if ($server.HasExited) {
            throw "Test server stopped. Check logs in $base\logs"
        }

        try {
            $health = Invoke-RestMethod `
                -Uri "$baseUrl/health" -TimeoutSec 2

            if ($health.status -eq 'UP') {
                $ready = $true
                break
            }
        } catch {
            Write-Host "Waiting for test server: $attempt"
        }

        Start-Sleep -Seconds 2
    }

    if (-not $ready) {
        throw "Test server did not become ready. Check $base\logs"
    }

    Write-Host 'Running Selenium against the new WAR on port 8082.'

    $env:JAVA_HOME = $JavaHome
    & $maven -B -Pselenium "-DbaseUrl=$baseUrl" `
        failsafe:integration-test failsafe:verify

    if ($LASTEXITCODE -ne 0) {
        $server.Refresh()
        Write-Host "Tomcat exited: $($server.HasExited)"

        if ($server.HasExited) {
            Write-Host "Tomcat exit code: $($server.ExitCode)"
        }

        try {
            $check = Invoke-RestMethod `
                -Uri "$baseUrl/health" -TimeoutSec 5
            Write-Host "Health after test failure: $($check.status)"
        } catch {
            Write-Host "Health check failed: $($_.Exception.Message)"
        }

        throw 'Selenium tests failed.'
    }

    $exitCode = 0
    Write-Host 'Selenium tests passed.'
} catch {
    Write-Host "ERROR: $($_.Exception.Message)"
} finally {
    if ($null -ne $server) {
        $server.Refresh()
        if (-not $server.HasExited) {
            Stop-Process -Id $server.Id -Force
            $server.WaitForExit()
        }
    }
    Write-Host "Test server logs: $base\logs"
}

exit $exitCode
