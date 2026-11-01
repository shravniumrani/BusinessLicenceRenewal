# Business Licence Renewal System - Deliverables Checklist

Updated: 1 November 2026.

Checked items have implementation or execution evidence.
Unchecked items require further evidence or are outside the verified scope.
Teacher approval and independent PR review are not claimed.

## 1. Planning and design
- [x] Problem statement, stakeholders, objectives and scope documented.
- [x] User stories, acceptance criteria and backlog documented.
- [x] Sprint plan, Kanban workflow and Definition of Done documented.
- [x] DevOps lifecycle, architecture and data model documented.
- [ ] Record teacher scope approval when received.
- [ ] Reconcile task-board/backlog status with completed implementation.

## 2. Git and collaboration
- [x] Repository, README, Maven layout and .gitignore present.
- [x] Issue templates, PR template and branching policy present.
- [x] Authentication and licence workflow feature branches merged.
- [x] Merge conflict resolution exercise recorded in Git history.
- [x] PR #6 merged Docker publishing and Ansible verification into development.
- [x] PR #7 merged development into main.
- [x] Annotated release tag v1.0.0 pushed for commit 6c290c2.
- [ ] Supply independent PR review evidence if required.
- [ ] Verify assignment issue/task-board evidence.

Release note: v1.0.0 identifies the Jenkins build #27 baseline.
Later homepage and documentation commits are on main.

## 3. Application
- [x] Java 21, Maven Wrapper and WAR packaging.
- [x] Jakarta Servlet application running on Tomcat 10.1.
- [x] Business Owner and Licensing Officer authentication.
- [x] Server-side role checks and owner-specific application access.
- [x] Create, view, edit, delete and search renewal applications.
- [x] Submitted applications can enter officer review.
- [x] Officer approval/rejection requires decision remarks.
- [x] Dashboard counts reflect application decisions.
- [x] H2 persistence and server-side validation.
- [x] Owner-to-officer approval journey demonstrated in the running app.
- [x] Homepage wording updated and deployed.

## 4. Jenkins and tests
- [x] Jenkins pipeline operational on localhost:8081.
- [x] Poll SCM configured; build #26 started by an SCM change.
- [x] Declarative Jenkinsfile performs checkout, build, test and packaging.
- [x] Deployment parameters include DEPLOY_TO_TOMCAT and TOMCAT_HOME.
- [x] WAR archived with fingerprints.
- [x] 17 unit tests and 3 Selenium browser journeys.
- [x] Selenium failure screenshot hook implemented.
- [x] Unit and Selenium test results published in Jenkins.
- [x] Failed tests block downstream deployment and publishing.
- [x] Deliberate defect caused build #24 to fail.
- [x] Defect reverted; build #25 succeeded.
- [x] Build #30 finished SUCCESS after a Docker network failure retry.
- [ ] Verify a separately preserved local Maven Selenium report if required.
- [ ] Preserve a triggered Selenium failure screenshot if required.

The pipeline uses deployment settings rather than an ENVIRONMENT
dev/staging/prod selector.

Evidence:
- evidence/quality-gate/build-24-failed.log
- evidence/quality-gate/build-25-passed.log
- evidence/jenkins/build-26-scm-trigger.log
- evidence/jenkins/build-30-success.log

## 5. Docker and registry
- [x] Dockerfile uses Tomcat 10.1.60 with Java 21.
- [x] Jenkins builds images tagged ci-BUILD_NUMBER.
- [x] Container deployment follows successful tests.
- [x] Application published on host port 8085 to container port 8080.
- [x] Persistent application data volume configured.
- [x] Container HTTP health checked during deployment.
- [x] Tested images published to shravniu/business-licence-renewal.
- [x] Git homepage change reached the running container through Jenkins.
- [ ] Verify a saved CLI lifecycle log covering logs, stop, restart and removal.
- [ ] Add a semantic Docker image tag if specifically required.

The Git release tag v1.0.0 and Docker ci-N tags are separate identifiers.

## 6. Ansible configuration and recovery
- [x] Inventory defines target-node with a local connection.
- [x] Playbook manages required packages, tomcat user/group and permissions.
- [x] Tomcat environment, directories and service control configured.
- [x] Application WAR deployment and restart verified.
- [x] Repeat deployment produced zero changed tasks.
- [x] Invalid candidate WAR rejected.
- [x] Previous healthy WAR restored and checked using SHA-256.
- [x] HTTP health after recovery returned UP.
- [x] Post-recovery run: ok=15, changed=0, unreachable=0, failed=0.
- [x] Configuration specification and execution evidence corrected.
- [ ] Clean VM provisioning is not demonstrated.
- [ ] Host firewall configuration is not demonstrated.

Verified run: 21da8e975647.
Evidence: evidence/ansible/21da8e975647/

Ansible ran in a separate isolated Linux container with real Tomcat.
Java and Tomcat were supplied by the base image.
This suite is separate from Jenkins and does not demonstrate database rollback.
Health endpoint: /business-licence-renewal/health.

## Submission status
Core application and CI/CD execution are demonstrated.
Remaining unchecked items identify evidence to verify or scope limitations;
they must not be represented as completed without supporting evidence.
