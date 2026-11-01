# Ansible configuration specification

## Target environment
- Isolated Linux container with real Apache Tomcat and Java 21.
- Base image: tomcat:10.1.60-jdk21-temurin.
- Inventory host: target-node.
- Connection: local inside the test container.
- This is a separate verification suite, not a Jenkins pipeline stage.

## Managed configuration
- Required packages and a dedicated tomcat user/group.
- Tomcat directory: /usr/local/tomcat.
- Application data directory: /var/lib/licence.
- Stable backup directory: /opt/licence-backups.
- Application WAR: business-licence-renewal.war.
- Directory ownership and restricted permissions.
- Tomcat environment settings and service control.
- Application HTTP health check on container port 8080.
- The verification container does not publish a host port.

## Deployment and recovery
The playbook compares WAR checksums and deploys only when needed.
Before replacing a healthy application, it saves the stable WAR.
A failed candidate triggers restoration of the stable WAR.
Recovery checks application health and verifies the restored SHA-256.
The rejected candidate remains reported as a failed deployment even
when recovery succeeds.

## Verification
Verified run: 21da8e975647, 1 November 2026.
The suite checked deployment, repeat-run idempotency, rejection of an
invalid WAR, restoration of the healthy WAR, and HTTP health after recovery.
Post-recovery repeat run: ok=15, changed=0, unreachable=0, failed=0.

Evidence: [execution logs](evidence/ansible/21da8e975647/).

## Scope
Java and Tomcat are supplied by the base container image.
This demonstrates configuration and application recovery in an isolated
container. It does not demonstrate clean VM provisioning, host firewall
configuration, or database rollback.
