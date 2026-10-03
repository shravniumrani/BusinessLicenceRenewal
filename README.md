# Business Licence Renewal System

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Build Tool](https://img.shields.io/badge/Maven-Wrapper%203.9.6-blue.svg)](https://maven.apache.org/)
[![Target Container](https://img.shields.io/badge/Apache%20Tomcat-10.1+-yellow.svg)](https://tomcat.apache.org/)
[![Scope Status](https://img.shields.io/badge/Scope%20Approval-Pending%20Teacher%20Review-amber.svg)](docs/problem_statement_and_scope.md)

An enterprise-grade web application and end-to-end DevOps automation pipeline for municipal business operating licence renewals.

This repository represents the **Stage 1: Initial Skeleton & Planning** baseline for the individual college DevOps assignment: **Jenkins Deployment for a Business Licence Renewal System**.

---

## 1. Project Overview & Eventual MVP

The Business Licence Renewal System is designed to replace manual, paper-based, or unstructured email licensing processes with a secure, role-restricted web portal backed by an automated CI/CD pipeline.

### Eventual MVP Core Capabilities
- **Server-Enforced Role-Based Access Control**: Predefined and registered roles for `BUSINESS_OWNER` and `LICENSING_OFFICER`.
- **Licence Application Lifecycle**: Create, view, update, and search licence renewal records.
- **Horizontal Data Scoping**: Business owners can strictly view and edit only their own applications.
- **Officer Review Workflow**: Authorized officers can review submitted renewal requests and approve or reject them with mandatory remarks.
- **Summary Metrics Dashboard**: Real-time counter metrics tracking total, pending, approved, and rejected applications.
- **Validation & Persistent Storage**: Strong server-side data validation and persistent transactional storage.

---

## 2. Assignment Deliverables Roadmap

| Stage | Focus Area | Deliverables | Status |
|---|---|---|---|
| **Stage 1 (Current)** | Architecture & Skeleton | Requirements checklist, problem statement, backlog, SRS, branching policy, Maven wrapper, Java 21 WAR skeleton, health endpoint, README. | **COMPLETED** |
| **Stage 2** | Core MVP Features | First feature branch (`feature/auth-and-models`), PR & merge, second feature branch (`feature/licence-crud-workflow`), merge conflict resolution, release tag `v1.0.0`. | Planned |
| **Stage 3** | Continuous Integration | Jenkins CI server, commit trigger/polling, declarative `Jenkinsfile` with environment parameters, automated WAR packaging and Tomcat deployment. | Planned |
| **Stage 4** | Automated Testing & Gates | 3–5 Selenium end-to-end journeys, screenshot capture on failure, Surefire reports, quality gate blocking deployment on defect injection. | Planned |
| **Stage 5** | Containerization | `Dockerfile`, container build, port mapping `8080:8080`, lifecycle management (logs, stop, restart, rm), Docker registry publishing. | Planned |
| **Stage 6** | Configuration Management | Ansible inventory & playbooks for provisioning, user hardening, service configuration, idempotency verification, health check, and rollback. | Planned |

---

## 3. Project Directory Structure

```text
BusinessLicenceRenewal/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md             # Defect reporting template
│   │   └── feature_request.md        # Feature request template
│   └── pull_request_template.md      # Pull request review template
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar         # Maven wrapper bootstrap binary
│       └── maven-wrapper.properties  # Distribution config (Maven 3.9.6)
├── docs/
│   ├── branching_policy.md           # Git branching strategy and conventions
│   ├── problem_statement_and_scope.md# Problem statement, stakeholders, scope (Pending Review)
│   ├── requirements_checklist.md     # 14-point assignment deliverables checklist
│   ├── srs_architecture_design.md    # SRS, Use-case, Architecture, Data Model, API
│   └── user_stories_and_backlog.md   # User stories, Gherkin criteria, backlog, DoD, DevOps diagram
├── src/
│   ├── main/
│   │   ├── java/com/gov/licence/
│   │   │   └── HealthCheckServlet.java # JSON health check endpoint (/health)
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml           # Jakarta EE 10 / Servlet 6.0 deployment descriptor
│   │       ├── css/
│   │       │   └── style.css         # Modern, accessible responsive styling
│   │       └── index.jsp             # System landing page and diagnostic dashboard
│   └── test/
│       └── java/com/gov/licence/
│           └── HealthCheckTest.java  # JUnit 5 environment and health test
├── .gitignore                        # Git exclusion rules for Java, IDEs, and OS
├── mvnw                              # Linux / macOS Maven wrapper script
├── mvnw.cmd                          # Windows CMD/PowerShell Maven wrapper script
├── pom.xml                           # Maven project descriptor (Java 21, Jakarta EE 10)
└── README.md                         # Project documentation and getting started guide
```

---

## 4. Local Development & Build Instructions (Windows)

### System Prerequisites
1. **Operating System**: Windows 10 or 11.
2. **Java JDK 21 LTS**: Installed at `C:\Program Files\Java\jdk-21`.
3. **Git**: Installed and available in terminal (`git --version`).
4. **Apache Maven**: **Not required on PATH**. The project includes the official Maven Wrapper (`.\mvnw.cmd`).

### Step-by-Step PowerShell Commands

Open PowerShell in the project directory (`c:\Users\SHRAVNI\Documents\BusinessLicenceRenewal`):

```powershell
# Step 1: Ensure JAVA_HOME points to your JDK 21 installation
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Step 2: Verify Java and Maven Wrapper
.\mvnw.cmd -v

# Step 3: Run automated unit tests
.\mvnw.cmd test

# Step 4: Clean, compile, and package the WAR artifact
.\mvnw.cmd clean package

# Step 5: Verify the generated WAR file
Get-Item target\business-licence-renewal.war
```

---

## 5. Deployment Options

### A. Deploy to Local Apache Tomcat 10.1+
Copy the generated WAR file to Tomcat's `webapps` folder:
```powershell
Copy-Item target\business-licence-renewal.war "C:\path\to\apache-tomcat-10.1.x\webapps\"
```
Access the application at:
- **Landing Page**: `http://localhost:8080/business-licence-renewal/`
- **Health Check API**: `http://localhost:8080/business-licence-renewal/health`

### B. Deploy via Docker (Upcoming in Stage 5)
Once the `Dockerfile` is added in Stage 5, the application will be containerized and run on port 8080.

---

## 6. Scope Approval Status

> **Notice**: As per academic project guidelines, the initial scope definition in [`docs/problem_statement_and_scope.md`](docs/problem_statement_and_scope.md) is marked as **PENDING TEACHER REVIEW**. Feature branches will be created and merged sequentially starting in Stage 2.
