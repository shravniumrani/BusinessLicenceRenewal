# Ansible actual execution evidence
Started: 2026-11-01T13:43:24.6976337+05:30
Target: licence-ansible-test-21da8e975647
Mode: isolated Linux container, local Ansible connection

Baseline WAR SHA256: 11d4168d1e0bb56ad2fdc7cf4f9575c3c8d801ff9454c10ea911e99606882dd6

## build
Exit code: 0
~~~text
#0 building with "desktop-linux" instance using docker driver
System.Management.Automation.RemoteException
#1 [internal] load build definition from Dockerfile
#1 transferring dockerfile: 293B 0.1s done
#1 DONE 0.1s
System.Management.Automation.RemoteException
#2 [internal] load metadata for docker.io/library/tomcat:10.1.60-jdk21-temurin
#2 ...
System.Management.Automation.RemoteException
#3 [auth] library/tomcat:pull token for registry-1.docker.io
#3 DONE 0.0s
System.Management.Automation.RemoteException
#2 [internal] load metadata for docker.io/library/tomcat:10.1.60-jdk21-temurin
#2 DONE 12.7s
System.Management.Automation.RemoteException
#4 [internal] load .dockerignore
#4 transferring context: 54B 0.0s done
#4 DONE 0.1s
System.Management.Automation.RemoteException
#5 [1/3] FROM docker.io/library/tomcat:10.1.60-jdk21-temurin@sha256:887953afba0434879427f18d011d4df26d49e1f5c9aad18b779ed738edf22cf3
#5 resolve docker.io/library/tomcat:10.1.60-jdk21-temurin@sha256:887953afba0434879427f18d011d4df26d49e1f5c9aad18b779ed738edf22cf3 0.0s done
#5 DONE 0.1s
System.Management.Automation.RemoteException
#6 [2/3] RUN apt-get update && apt-get install -y --no-install-recommends ansible-core python3 python3-apt curl util-linux && rm -rf /var/lib/apt/lists/*
#6 CACHED
System.Management.Automation.RemoteException
#7 [3/3] WORKDIR /ansible
#7 CACHED
System.Management.Automation.RemoteException
#8 exporting to image
#8 exporting layers 0.0s done
#8 exporting manifest sha256:d09886e9b5b5a3d814366b78db04798cdc9054fb5888f170e13921c0989e5c75 done
#8 exporting config sha256:6cf86d53702ce853dbd81cafcb7b1854dd37479f714a286c77de743987611407 done
#8 exporting attestation manifest sha256:7d81631d6aea78586470b2edb3337d91253b95a1553246646b41fec81164ea43 0.0s done
#8 exporting manifest list sha256:129ea48e7d9584e4746cbcccae15666312e10bf01792b5e53f237e248e6f4ba3 0.0s done
#8 naming to docker.io/library/licence-ansible-verified:1 done
#8 unpacking to docker.io/library/licence-ansible-verified:1 0.0s done
#8 DONE 0.2s
~~~

## create
Exit code: 0
~~~text
de393edba747e0c11d54c2e32da77a215225f6d46f6d15128878267956e1e593
~~~

## provision
Exit code: 0
~~~text

PLAY [Configure real Tomcat and deploy with automatic recovery] ****************

TASK [Ensure required operating system packages] *******************************
ok: [target-node]

TASK [Create dedicated Tomcat group] *******************************************
changed: [target-node]

TASK [Create dedicated Tomcat user] ********************************************
changed: [target-node]

TASK [Assign Tomcat tree to service user] **************************************
changed: [target-node]

TASK [Create restricted state directories] *************************************
changed: [target-node] => (item=/var/lib/licence)
changed: [target-node] => (item=/opt/licence-backups)
changed: [target-node] => (item=/usr/local/tomcat/temp)
changed: [target-node] => (item=/usr/local/tomcat/work)

TASK [Configure Tomcat application environment] ********************************
changed: [target-node]

TASK [Install real Tomcat service control] *************************************
changed: [target-node]

TASK [Inspect candidate WAR] ***************************************************
ok: [target-node]

TASK [Require candidate artifact] **********************************************
ok: [target-node] => {
    "changed": false,
    "msg": "All assertions passed"
}

TASK [Inspect currently installed WAR] *****************************************
ok: [target-node]

TASK [Set deployment change decision] ******************************************
ok: [target-node]

TASK [Require existing release to be healthy before taking backup] *************
skipping: [target-node]

TASK [Save actual previous healthy WAR before replacement] *********************
skipping: [target-node]

TASK [Stop Tomcat before changed deployment] ***********************************
changed: [target-node]

TASK [Remove old expanded application only on change] **************************
ok: [target-node]

TASK [Deploy WAR artifact] *****************************************************
changed: [target-node]

TASK [Inspect Tomcat process] **************************************************
ok: [target-node]

TASK [Start real Tomcat if required] *******************************************
changed: [target-node]

TASK [Verify deployed application] *********************************************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (30 retries left).
ok: [target-node]

PLAY RECAP *********************************************************************
target-node                : ok=18   changed=9    unreachable=0    failed=0    skipped=2    rescued=0    ignored=0   

~~~

## idempotency
Exit code: 0
~~~text

PLAY [Configure real Tomcat and deploy with automatic recovery] ****************

TASK [Ensure required operating system packages] *******************************
ok: [target-node]

TASK [Create dedicated Tomcat group] *******************************************
ok: [target-node]

TASK [Create dedicated Tomcat user] ********************************************
ok: [target-node]

TASK [Assign Tomcat tree to service user] **************************************
ok: [target-node]

TASK [Create restricted state directories] *************************************
ok: [target-node] => (item=/var/lib/licence)
ok: [target-node] => (item=/opt/licence-backups)
ok: [target-node] => (item=/usr/local/tomcat/temp)
ok: [target-node] => (item=/usr/local/tomcat/work)

TASK [Configure Tomcat application environment] ********************************
ok: [target-node]

TASK [Install real Tomcat service control] *************************************
ok: [target-node]

TASK [Inspect candidate WAR] ***************************************************
ok: [target-node]

TASK [Require candidate artifact] **********************************************
ok: [target-node] => {
    "changed": false,
    "msg": "All assertions passed"
}

TASK [Inspect currently installed WAR] *****************************************
ok: [target-node]

TASK [Set deployment change decision] ******************************************
ok: [target-node]

TASK [Require existing release to be healthy before taking backup] *************
skipping: [target-node]

TASK [Save actual previous healthy WAR before replacement] *********************
skipping: [target-node]

TASK [Stop Tomcat before changed deployment] ***********************************
skipping: [target-node]

TASK [Remove old expanded application only on change] **************************
skipping: [target-node]

TASK [Deploy WAR artifact] *****************************************************
ok: [target-node]

TASK [Inspect Tomcat process] **************************************************
ok: [target-node]

TASK [Start real Tomcat if required] *******************************************
skipping: [target-node]

TASK [Verify deployed application] *********************************************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
ok: [target-node]

PLAY RECAP *********************************************************************
target-node                : ok=15   changed=0    unreachable=0    failed=0    skipped=5    rescued=0    ignored=0   

~~~

## rejected-candidate-and-recovery
Exit code: 2
~~~text

PLAY [Configure real Tomcat and deploy with automatic recovery] ****************

TASK [Ensure required operating system packages] *******************************
ok: [target-node]

TASK [Create dedicated Tomcat group] *******************************************
ok: [target-node]

TASK [Create dedicated Tomcat user] ********************************************
ok: [target-node]

TASK [Assign Tomcat tree to service user] **************************************
ok: [target-node]

TASK [Create restricted state directories] *************************************
ok: [target-node] => (item=/var/lib/licence)
ok: [target-node] => (item=/opt/licence-backups)
ok: [target-node] => (item=/usr/local/tomcat/temp)
ok: [target-node] => (item=/usr/local/tomcat/work)

TASK [Configure Tomcat application environment] ********************************
ok: [target-node]

TASK [Install real Tomcat service control] *************************************
ok: [target-node]

TASK [Inspect candidate WAR] ***************************************************
ok: [target-node]

TASK [Require candidate artifact] **********************************************
ok: [target-node] => {
    "changed": false,
    "msg": "All assertions passed"
}

TASK [Inspect currently installed WAR] *****************************************
ok: [target-node]

TASK [Set deployment change decision] ******************************************
ok: [target-node]

TASK [Require existing release to be healthy before taking backup] *************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
ok: [target-node]

TASK [Save actual previous healthy WAR before replacement] *********************
changed: [target-node]

TASK [Stop Tomcat before changed deployment] ***********************************
changed: [target-node]

TASK [Remove old expanded application only on change] **************************
changed: [target-node]

TASK [Deploy WAR artifact] *****************************************************
changed: [target-node]

TASK [Inspect Tomcat process] **************************************************
ok: [target-node]

TASK [Start real Tomcat if required] *******************************************
changed: [target-node]

TASK [Verify deployed application] *********************************************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (30 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (29 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (28 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (27 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (26 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (25 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (24 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (23 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (22 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (21 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (20 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (19 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (18 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (17 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (16 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (15 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (14 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (13 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (12 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (11 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (10 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (9 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (8 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (7 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (6 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (5 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (4 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (3 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (2 retries left).
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (1 retries left).
[ERROR]: Task failed: Module failed: Status code was 404 and not [200]: HTTP Error 404: 
Origin: /ansible/health.yml:2:3

1 ---
2 - name: Verify actual application HTTP health
    ^ column 3

fatal: [target-node]: FAILED! => {"attempts": 30, "changed": false, "connection": "close", "content": "<!doctype html><html lang=\"en\"><head><title>HTTP Status 404 ΓÇô Not Found</title><style type=\"text/css\">body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}</style></head><body><h1>HTTP Status 404 ΓÇô Not Found</h1><hr class=\"line\" /><p><b>Type</b> Status Report</p><p><b>Description</b> The origin server did not find a current representation for the target resource or is not willing to disclose that one exists.</p><hr class=\"line\" /><h3>Apache Tomcat/10.1.60</h3></body></html>", "content_language": "en", "content_length": "683", "content_type": "text/html;charset=utf-8", "date": "Sun, 01 Nov 2026 08:16:06 GMT", "elapsed": 0, "msg": "Status code was 404 and not [200]: HTTP Error 404: ", "redirected": false, "status": 404, "url": "http://127.0.0.1:8080/business-licence-renewal/health"}

TASK [Recover previous healthy release] ****************************************
included: /ansible/rollback_tasks.yml for target-node

TASK [Inspect previous healthy WAR backup] *************************************
ok: [target-node]

TASK [Require a real backup before stopping Tomcat] ****************************
ok: [target-node] => {
    "changed": false,
    "msg": "All assertions passed"
}

TASK [Stop Tomcat for recovery] ************************************************
changed: [target-node]

TASK [Remove candidate expanded application] ***********************************
ok: [target-node]

TASK [Restore previous healthy WAR] ********************************************
changed: [target-node]

TASK [Start restored Tomcat] ***************************************************
changed: [target-node]

TASK [Verify restored application health] **************************************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
FAILED - RETRYING: [target-node]: Verify actual application HTTP health (30 retries left).
ok: [target-node]

TASK [Read restored WAR checksum] **********************************************
ok: [target-node]

TASK [Verify exact restoration] ************************************************
ok: [target-node] => {
    "changed": false,
    "msg": "ROLLBACK VERIFIED - exact previous WAR restored and HTTP health is UP."
}

TASK [Keep rejected deployment marked as failure after successful recovery] ****
[ERROR]: Task failed: Action failed: CANDIDATE REJECTED - previous healthy release restored. Deployment remains failed.
Origin: /ansible/playbook.yml:141:11

139         - name: Recover previous healthy release
140           ansible.builtin.include_tasks: rollback_tasks.yml
141         - name: Keep rejected deployment marked as failure after successful recovery
              ^ column 11

fatal: [target-node]: FAILED! => {"changed": false, "msg": "CANDIDATE REJECTED - previous healthy release restored. Deployment remains failed."}

PLAY RECAP *********************************************************************
target-node                : ok=31   changed=8    unreachable=0    failed=1    skipped=0    rescued=1    ignored=0   

~~~

## restored-hash
Exit code: 0
~~~text
11d4168d1e0bb56ad2fdc7cf4f9575c3c8d801ff9454c10ea911e99606882dd6  /usr/local/tomcat/webapps/business-licence-renewal.war
~~~

## restored-health
Exit code: 0
~~~text
{
  "status": "UP",
  "service": "Business Licence Renewal System",
  "version": "0.1.0-SNAPSHOT",
  "timestamp": "2026-11-01T08:16:25.486325373Z",
  "javaVersion": "21.0.12.1",
  "javaVendor": "Eclipse Adoptium",
  "os": "Linux"
}
~~~

## post-recovery-idempotency
Exit code: 0
~~~text

PLAY [Configure real Tomcat and deploy with automatic recovery] ****************

TASK [Ensure required operating system packages] *******************************
ok: [target-node]

TASK [Create dedicated Tomcat group] *******************************************
ok: [target-node]

TASK [Create dedicated Tomcat user] ********************************************
ok: [target-node]

TASK [Assign Tomcat tree to service user] **************************************
ok: [target-node]

TASK [Create restricted state directories] *************************************
ok: [target-node] => (item=/var/lib/licence)
ok: [target-node] => (item=/opt/licence-backups)
ok: [target-node] => (item=/usr/local/tomcat/temp)
ok: [target-node] => (item=/usr/local/tomcat/work)

TASK [Configure Tomcat application environment] ********************************
ok: [target-node]

TASK [Install real Tomcat service control] *************************************
ok: [target-node]

TASK [Inspect candidate WAR] ***************************************************
ok: [target-node]

TASK [Require candidate artifact] **********************************************
ok: [target-node] => {
    "changed": false,
    "msg": "All assertions passed"
}

TASK [Inspect currently installed WAR] *****************************************
ok: [target-node]

TASK [Set deployment change decision] ******************************************
ok: [target-node]

TASK [Require existing release to be healthy before taking backup] *************
skipping: [target-node]

TASK [Save actual previous healthy WAR before replacement] *********************
skipping: [target-node]

TASK [Stop Tomcat before changed deployment] ***********************************
skipping: [target-node]

TASK [Remove old expanded application only on change] **************************
skipping: [target-node]

TASK [Deploy WAR artifact] *****************************************************
ok: [target-node]

TASK [Inspect Tomcat process] **************************************************
ok: [target-node]

TASK [Start real Tomcat if required] *******************************************
skipping: [target-node]

TASK [Verify deployed application] *********************************************
included: /ansible/health.yml for target-node

TASK [Verify actual application HTTP health] ***********************************
ok: [target-node]

PLAY RECAP *********************************************************************
target-node                : ok=15   changed=0    unreachable=0    failed=0    skipped=5    rescued=0    ignored=0   

~~~

## Suite outcome
Passed: True
A failed candidate is intentional; overall success requires actual restoration checks.