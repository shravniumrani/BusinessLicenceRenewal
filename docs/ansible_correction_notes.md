# Ansible correction and verification scope

The prior ansible_execution_evidence.md is historical and unreliable: it contains a failed rollback followed by a hard-coded success summary. Do not submit that summary as proof.

The corrected runner keeps all runs on ONE isolated Linux container. Tomcat 10.1.60 and Java 21 come from the official Tomcat base image. Ansible configures a dedicated service user, package state, directory permissions, application environment, WAR deployment and real service control. Local connection is intentional: controller and managed demo node share this container. This is a container demonstration, not clean VM provisioning or remote SSH provisioning.

The runner checks:
1. Initial deployment with real HTTP 200 and JSON status UP.
2. A second identical run with changed=0, unreachable=0 and failed=0.
3. An intentionally invalid WAR causing failed deployment and automatic recovery.
4. Exact restored WAR SHA256 matching the original healthy baseline.
5. Restored application HTTP health UP.
6. A final identical baseline deployment with zero changes.

The rejected candidate must return Ansible exit code 2 even after recovery. The runner accepts that one expected failure only if recovery messages, checksum and HTTP checks also pass. Every other nonzero exit fails the suite. Evidence is generated from actual output under target/ansible-evidence/<run-id>/results.md and accompanying logs. No passing result has been assumed in this patch.

No host port is published for this test container. Health is checked inside the target. Host firewall provisioning is not demonstrated. No existing application container or application data volume is modified. The test target is removed after the suite; logs remain.

This is WAR rollback with the same application schema; it does not demonstrate database migration rollback. A corrupt WAR tests deployment recovery, not a failing unit test quality gate. The teacher's separate quality gate evidence is still required.

Jenkinsfile correction: delimit ${attempt} before the colon inside the publication message to avoid PowerShell variable parsing failure. Registry network failures are a separate issue and are not claimed resolved by this correction.

The Ansible suite is run separately first. It has not yet been inserted into the main Jenkins pipeline. After a passing standalone run, it can be added after Package/Selenium and its target/ansible-evidence/** logs archived. Do not mark tasks complete without checking the real results against the teacher's full task sheet.
