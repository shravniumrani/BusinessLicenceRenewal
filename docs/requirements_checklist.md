# Business Licence Renewal System — DevOps Project Deliverables Checklist

This checklist tracks all 14 assignment deliverables across the software engineering and DevOps lifecycle for the individual college project: **Jenkins Deployment for a Business Licence Renewal System**.

---

## Deliverables Status Matrix

| ID | Assignment Deliverable Area | Key Artifacts & Verification Criteria | Target Milestone | Current Status |
|---|---|---|---|---|
| **DEL-01** | Problem Statement & Scope | Problem statement, stakeholders, objectives, constraints, MVP scope vs. out-of-scope boundaries. Marked as pending teacher review. | Stage 1 (Initial Setup) | **COMPLETED (Pending Review)** |
| **DEL-02** | Agile Planning & DevOps Lifecycle | User stories with Gherkin acceptance criteria, MoSCoW product backlog, sprint plan, Kanban workflow, Definition of Done (DoD), and end-to-end DevOps lifecycle diagram. | Stage 1 (Initial Setup) | **COMPLETED** |
| **DEL-03** | SRS, Architecture & Data Model | SRS summary (FRs/NFRs), Mermaid Use-Case diagram, System Architecture diagram, ER data model, REST/Web endpoint table, local setup instructions. | Stage 1 (Initial Setup) | **COMPLETED** |
| **DEL-04** | Repository & Collaboration Setup | Git repository initialized, `.gitignore`, production-ready `README.md`, standard Maven directory layout, GitHub issue templates, PR template, Git branching policy. | Stage 1 (Initial Setup) | **COMPLETED** |
| **DEL-05** | First Feature Branch & Merge | Branch `feature/auth-and-models` created, local implementation of authentication and domain entities, PR reviewed, cleanly merged into `development`. | Stage 2 (Core Features) | **PLANNED** |
| **DEL-06** | Remaining MVP & Release Tag | Branch `feature/licence-crud-workflow`, search/dashboard/officer review implementation, merge conflict resolution simulation, release tag `v1.0.0` cut on `main`. | Stage 2 (Core Features) | **PLANNED** |
| **DEL-07** | Jenkins CI Job & Build Artifact | Jenkins CI freestyle or pipeline job with commit-triggered polling / webhook, automated clean compile, package, and archived `business-licence-renewal.war`. | Stage 3 (CI Automation) | **PLANNED** |
| **DEL-08** | Declarative Jenkinsfile | Pipeline with parameters (`ENVIRONMENT=dev/staging/prod`), stages: Checkout, Build, Test, Package, Archive, and Deployment to Apache Tomcat 10.1+. | Stage 3 (CI Automation) | **PLANNED** |
| **DEL-09** | Selenium Journey Automation | 3–5 automated end-to-end Selenium journeys (Owner registration/login, Licence submission, Officer review/decision, Search filtering), assertions, test data provider, execution via Maven, failure screenshot hooks. | Stage 4 (Test Automation) | **PLANNED** |
| **DEL-10** | Quality Gate & Defect Demonstration | Jenkins JUnit/Surefire test reports, build/deployment strictly blocked on test failure, deliberate defect injected and demonstrated, subsequent fix and green rerun. | Stage 4 (Test Automation) | **PLANNED** |
| **DEL-11** | Docker Containerization | Multi-stage/Tomcat `Dockerfile`, container build, image tagging, run with port mapping (`8080:8080`), log streaming, stop, restart, and clean removal verification. | Stage 5 (Containers & Registry) | **PLANNED** |
| **DEL-12** | Versioned Image & Pipeline Deploy | Automated tagging (`business-licence-renewal:1.0.0`), Docker registry push/local registry publish, automated container redeployment triggered after test suite passes. | Stage 5 (Containers & Registry) | **PLANNED** |
| **DEL-13** | Ansible Configuration Management | Ansible inventory, playbook defining Tomcat user, system packages, directory hierarchy, permission hardening, war transfer, firewall ports, and service management. | Stage 6 (Ansible & Operations) | **PLANNED** |
| **DEL-14** | Provisioning, Idempotency & Rollback | Clean VM/host provisioning demonstration, two consecutive identical runs proving zero change (idempotency), HTTP health-check verification, automated rollback to prior stable war. | Stage 6 (Ansible & Operations) | **PLANNED** |

---

## Detailed Requirement Breakdown & Verification Evidence

### 1. Planning & Design Phase (Stages 1–2)
- [x] **Problem Statement**: Defined in `docs/problem_statement_and_scope.md`.
- [x] **Stakeholders & Objectives**: Documented with role responsibilities and system boundaries.
- [x] **Scope Approval**: Explicitly marked as **PENDING TEACHER REVIEW** in documentation.
- [x] **User Stories & Acceptance Criteria**: Standard Gherkin syntax (`Given-When-Then`) covering both Owner and Officer workflows in `docs/user_stories_and_backlog.md`.
- [x] **Sprint & Kanban Plan**: Documented 5-sprint agile delivery plan and workflow state transitions.
- [x] **Definition of Done (DoD)**: Strict quality criteria defined for code, tests, documentation, and CI status.
- [x] **DevOps Lifecycle Diagram**: Visualized using Mermaid flowchart covering Plan -> Monitor.
- [x] **SRS & Diagrams**: Functional/Non-Functional requirements, Use-Case diagram, System Architecture diagram, and Data Model in `docs/srs_architecture_design.md`.

### 2. Version Control & Git Strategy (Stages 1–2)
- [x] Local Git repository initialized with `main` branch.
- [x] Git branching policy (`main`, `development`, `feature/*`, `hotfix/*`) in `docs/branching_policy.md`.
- [x] GitHub issue templates for bug reports and feature requests in `.github/ISSUE_TEMPLATE/`.
- [x] Pull request template configured in `.github/pull_request_template.md`.
- [ ] Feature branch 1 (`feature/auth-and-models`): real commit history, PR, merge into `development`.
- [ ] Feature branch 2 (`feature/licence-crud-workflow`): real commit history, merge conflict resolution demonstration, merge into `development` and `main`, release tag `v1.0.0`.

### 3. Application Architecture & Local Build (Stage 1 & Stage 2)
- [x] Java 21 LTS compatibility.
- [x] Official Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `maven-wrapper.jar`) included for zero-PATH dependency.
- [x] Clean WAR packaging targeting Apache Tomcat 10.1+ (Jakarta EE 10 / Servlet 6.0).
- [x] Landing page (`src/main/webapp/index.jsp`) with system status and responsive styling.
- [x] Health-check servlet (`/health`) returning application uptime and metadata.
- [ ] Authentication layer: Business Owner and Licensing Officer roles enforced strictly on server side.
- [ ] CRUD operations for Licence Renewal applications (Create, Read, Update, Search).
- [ ] Data scoping: Business Owners can view/edit only their own submissions.
- [ ] Review workflow: Officers can inspect, approve, or reject submissions with mandatory remarks.
- [ ] Summary Dashboard: Application metrics (Total, Submitted, Under Review, Approved, Rejected).
- [ ] Persistence & validation: Input sanitization, business rule validation, persistent storage.

### 4. Continuous Integration & Pipeline Automation (Stage 3 & Stage 4)
- [ ] Jenkins service operational (local host or containerized).
- [ ] CI Job configuration with automated triggers (Git polling or push hooks).
- [ ] Jenkinsfile scripted/declarative pipeline with environment parameter (`dev`, `staging`, `prod`).
- [ ] Automated artifact archiving (`target/*.war`).
- [ ] Selenium Test Suite (3–5 end-to-end user journeys).
- [ ] Automated failure screenshot capture on test assertion failure.
- [ ] Surefire and JUnit test report publishing in Jenkins UI.
- [ ] Quality Gate: Pipeline halts and blocks deployment when any unit or UI test fails.
- [ ] Deliberate defect demonstration: Documented break, pipeline block, fix, and green rerun.

### 5. Containerization & Registry (Stage 5)
- [ ] `Dockerfile` for Apache Tomcat 10.1 + Java 21 runtime.
- [ ] Build, tag (`business-licence-renewal:latest` and semantic versions), run with port mapping `8080:8080`.
- [ ] Container CLI demonstration: `docker logs`, `docker stop`, `docker restart`, `docker rm`.
- [ ] Image registry push (Docker Hub or local private registry).
- [ ] Post-test automated container deployment in Jenkins pipeline.

### 6. Configuration Management & Infrastructure as Code (Stage 6)
- [ ] Ansible inventory defining target deployment host(s).
- [ ] Ansible playbook provisioning Java 21, Tomcat service, user creation, file permissions, and firewall rules.
- [ ] Automated war artifact deployment and service restart.
- [ ] Idempotency verification: Running the playbook twice leaves zero changed tasks on second execution.
- [ ] Automated health-check verification (`curl http://<host>:8080/health`).
- [ ] Automated rollback mechanism restoring the previous known-good release if deployment fails.
