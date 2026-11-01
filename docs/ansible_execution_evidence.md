# Ansible execution evidence

Verified run: 21da8e975647, 1 November 2026.

Actual execution logs are saved in:
[evidence/ansible/21da8e975647](evidence/ansible/21da8e975647/)

The verification suite passed:
- Application deployment and HTTP health check.
- Repeat deployment with no configuration changes.
- Rejection of an invalid candidate WAR.
- Restoration of the previous healthy WAR, checked by SHA-256.
- Application health after recovery: UP.
- Post-recovery repeat run: ok=15, changed=0, unreachable=0, failed=0.

See results.md and the individual logs in the evidence directory.

Scope: Ansible ran against an isolated Linux container with real Tomcat.
This was a separate verification suite, not an Ansible stage in Jenkins.
It does not demonstrate provisioning a clean VM or database rollback.

This document replaces an earlier draft whose success summary contradicted
its failed rollback output.
