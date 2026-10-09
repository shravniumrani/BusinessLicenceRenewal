pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    parameters {
        booleanParam(
            name: 'DEPLOY_TO_TOMCAT',
            defaultValue: false,
            description: 'Deploy after tests pass. Start Tomcat first.'
        )
        string(
            name: 'TOMCAT_HOME',
            defaultValue: 'C:/Users/SHRAVNI/Documents/apache-tomcat-10.1.60',
            description: 'Path to the local Tomcat folder'
        )
    }

    environment {
        JAVA_HOME = 'C:/Program Files/Java/jdk-21'
        PATH = "${JAVA_HOME}/bin;${env.PATH}"
        HEALTH_URL = 'http://localhost:8080/business-licence-renewal/health'
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'call mvnw.cmd -B clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'call mvnw.cmd -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                bat 'call mvnw.cmd -B -DskipTests package'
                archiveArtifacts(
                    artifacts: 'target/*.war',
                    fingerprint: true
                )
            }
        }

                 stage('Selenium Tests') {
            steps {
                bat '''
                    powershell.exe -NoProfile -ExecutionPolicy Bypass -File scripts\\run-selenium.ps1 -TomcatHome "%TOMCAT_HOME%" -JavaHome "%JAVA_HOME%"
                '''
            }
            post {
                always {
                    junit(
                        testResults: 'target/failsafe-reports/TEST-*.xml',
                        allowEmptyResults: true
                    )
                    archiveArtifacts(
                        artifacts: 'target/selenium-screenshots/*.png,target/selenium-tomcat-*/logs/**',
                        allowEmptyArchive: true
                    )
                }
            }
        }
        stage('Docker Build') {
            steps {
                powershell '''
                    $ErrorActionPreference = 'Stop'
                    $docker = Join-Path $env:ProgramFiles 'Docker/Docker/resources/bin/docker.exe'

                    & $docker build -t "business-licence-renewal:ci-$env:BUILD_NUMBER" .
                    if ($LASTEXITCODE -ne 0) {
                        throw 'Docker image build failed.'
                    }
                '''
            }
        }
                stage('Deploy to Docker') {
            steps {
                bat '''
                    powershell.exe -NoProfile -ExecutionPolicy Bypass -File scripts/deploy-docker.ps1 -Image business-licence-renewal:ci-%BUILD_NUMBER%
                '''
            }
        }
stage('Deploy to Tomcat') {
            when {
                expression { params.DEPLOY_TO_TOMCAT }
            }
            steps {
                powershell '''
                    $ErrorActionPreference = 'Stop'

                    $webapps = Join-Path $env:TOMCAT_HOME 'webapps'
                    if (-not (Test-Path $webapps -PathType Container)) {
                        throw "Tomcat webapps folder not found: $webapps"
                    }

                    $connection = New-Object System.Net.Sockets.TcpClient
                    try {
                        $connection.Connect('localhost', 8080)
                    } catch {
                        throw 'Start Tomcat on port 8080 before deploying.'
                    } finally {
                        $connection.Dispose()
                    }

                    Copy-Item 'target/business-licence-renewal.war' `
                        -Destination $webapps -Force

                    Start-Sleep -Seconds 10

                    for ($attempt = 1; $attempt -le 24; $attempt++) {
                        try {
                            $health = Invoke-RestMethod `
                                -Uri $env:HEALTH_URL -TimeoutSec 5

                            if ($health.status -eq 'UP') {
                                Write-Host 'Application health check passed.'
                                exit 0
                            }
                        } catch {
                            Write-Host "Waiting for application: attempt $attempt"
                        }
                        Start-Sleep -Seconds 5
                    }

                    throw 'Application health check failed after deployment.'
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the failed stage in Console Output.'
        }
    }
}


