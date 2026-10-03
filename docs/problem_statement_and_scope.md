# Problem Statement, Objectives, Constraints & Scope

> **APPROVAL STATUS: PENDING TEACHER REVIEW**  
> *Note: This document outlines the proposed scope, technical architecture, and implementation boundary for the college DevOps project. Formal scope sign-off is pending review and approval by the course instructor.*

---

## 1. Problem Statement

Municipal and regional trade regulatory authorities face significant administrative friction and processing delays in managing business operating licences. The traditional renewal process suffers from critical systemic bottlenecks:

1. **Manual and Fragmented Submissions**: Business owners frequently submit renewal applications via paper forms, physical office visits, or unstructured email attachments, resulting in lost records, illegible documentation, and data-entry backlogs.
2. **Lack of Transparency and Status Tracking**: Applicants have no real-time visibility into whether their application has been received, queued, audited, or rejected. This leads to excessive follow-ups and inquiries handled manually by front-desk staff.
3. **Absence of Role-Enforced Security**: In manual or loosely controlled shared environments, privacy breaches can occur where unauthorized parties view proprietary financial or registration details of competitor enterprises.
4. **Disorganized Officer Review Workflows**: Licensing officers lack a consolidated queue with audit remarks, leading to inconsistent application of regulatory criteria and lack of accountability for approval or rejection decisions.
5. **Slow and Error-Prone Deployments**: Institutional licensing software has historically relied on manual, ad-hoc server deployments without continuous integration, test automation, containerized portability, or automated rollback capabilities, resulting in frequent production outages during updates.

The **Business Licence Renewal System** resolves these operational deficits by delivering a secure, role-restricted web portal backed by a comprehensive DevOps automation pipeline (Jenkins CI/CD, Selenium automated regression, Docker containerization, and Ansible configuration management).

---

## 2. Stakeholders & Personas

| Stakeholder Role | Description & Responsibilities | Key Needs / Pain Points |
|---|---|---|
| **Business Owner (Applicant)** | Commercial entity owner or authorized representative applying to renew an expiring operating licence. | Needs an intuitive self-service portal to submit renewal requests, track status in real-time, view only their own records, and edit pending drafts. |
| **Licensing Officer (Reviewer/Approver)** | Municipal authority employee responsible for compliance audit, inspecting supporting business records, and deciding on renewal. | Needs a centralized dashboard to search and filter applications, inspect applicant details, and approve or reject submissions with mandatory audit remarks. |
| **DevOps / Release Engineer (Student/Admin)** | Engineer responsible for build automation, continuous testing, container lifecycle, and server configuration. | Needs repeatable build scripts (Maven Wrapper), automated Jenkins pipelines, Docker packaging, and idempotent Ansible deployment with rollback. |
| **Academic Instructor / Evaluator** | College course evaluator reviewing software quality, Git discipline, CI/CD pipeline automation, and operational robustness. | Requires clear requirement traceability, valid Git commit progression, working local setup, test reports, and configuration management evidence. |

---

## 3. Project Objectives & Success Criteria

1. **Digital Self-Service Application Workflow**:
   - Provide an accessible web interface for business owners to submit and manage renewal requests.
   - Enforce rigorous server-side validation on business registration numbers, turnover, employee counts, and expiry dates.

2. **Server-Enforced Role-Based Access Control (RBAC)**:
   - Differentiate strictly between `BUSINESS_OWNER` and `LICENSING_OFFICER` roles on the server side (HTTP session & security filters).
   - Strict horizontal data isolation: Business owners must never be able to access, view, or modify records belonging to other businesses.
   - Officers possess authority to view all records in the jurisdiction and make status decisions.

3. **Officer Governance & Decision Auditing**:
   - Enable officers to approve or reject renewals, mandating justification remarks for every rejection or approval.
   - Maintain status timestamps and reviewer metadata for end-to-end accountability.

4. **Summary Management Dashboard**:
   - Display real-time aggregated metrics (Total Applications, Pending Review, Approved, Rejected) for operational awareness.

5. **Full-Lifecycle DevOps Automation**:
   - Automated compilation and packaging via Maven Wrapper on Java 21 LTS without requiring Maven pre-installed on the host PATH.
   - Jenkins CI pipeline with checkout, build, package, archiving, and automated Tomcat deployment.
   - Automated Selenium regression tests with quality gate enforcement (blocking deployment upon failure).
   - Containerization via Docker for deterministic runtime environments.
   - Ansible automation for idempotent provisioning, health-checking, and rollback.

---

## 4. Technical & Operational Constraints

1. **Platform & Operating System**: Host development on Windows 11 (PowerShell terminal). Target runtime environments support Windows and Linux (Ubuntu 22.04 LTS for Docker and Ansible targets).
2. **Java Version**: Strict adherence to **Java JDK 21 LTS**. No deprecated Java 8/11 libraries.
3. **Web Container**: **Apache Tomcat 10.1+** (Jakarta EE 10 / Servlet 6.0 specification). All servlet imports must use `jakarta.servlet.*` rather than legacy `javax.servlet.*`.
4. **Build System**: **Apache Maven 3.9+** managed strictly via the project's **Maven Wrapper** (`mvnw` / `mvnw.cmd`) to guarantee zero-PATH installation dependency.
5. **Database / Persistence**: Persistent file-based or embedded relational storage (H2 database engine / JDBC) ensuring zero external database installation overhead for local evaluation, while maintaining strict ACID compliance.
6. **No Fake Deliverables**: Git history must reflect genuine staged development without fabricated commits, fake pull requests, or artificially pasted logs.

---

## 5. Scope Definition: MVP vs. Future Extensions

```
+---------------------------------------------------------------------------------+
|                                PROJECT SCOPE                                    |
+---------------------------------------------------------------------------------+
|  IN-SCOPE (MVP) - PENDING TEACHER REVIEW       |  OUT-OF-SCOPE (FUTURE RELEASES)|
+------------------------------------------------+--------------------------------+
| [x] Server-enforced Owner & Officer Auth       | [ ] Payment Gateway Integration|
| [x] Business Licence CRUD Operations           | [ ] SMS / Email OTP 2FA        |
| [x] Strict Data Scoping (Owner sees own data)  | [ ] Multi-tenant Cloud Hosting |
| [x] Officer Approval/Rejection with Remarks    | [ ] Geographic GIS Mapping     |
| [x] Search & Filter by Reg No, Name, Status   | [ ] Advanced PDF Watermarking  |
| [x] Real-time Summary Metrics Dashboard        | [ ] Biometric Verification     |
| [x] Server-side Input Validation & Persistence | [ ] Third-Party Tax API Sync   |
| [x] Maven Wrapper Build & Packaging for Tomcat |                                |
| [x] Jenkins CI Pipeline with Parameterization  |                                |
| [x] 3-5 Automated Selenium Journeys & Gates    |                                |
| [x] Dockerfile & Container Lifecycle Demo      |                                |
| [x] Ansible Provisioning, Idempotency, Rollback|                                |
+---------------------------------------------------------------------------------+
```

### In-Scope (MVP Core Deliverables)
- User Authentication: Secure login/logout supporting predefined and registered accounts for `BUSINESS_OWNER` and `LICENSING_OFFICER`.
- Renewal Record Management: Application form capturing Business Name, Registration Number, Trade Category, Annual Turnover, Current Licence Expiry Date, and Contact Information.
- Ownership Isolation: Server checks ensure an owner cannot read or mutate another applicant's record via ID tampering.
- Officer Review Queue: Tabular list with status filters (`SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`), detailed view, and decision actions requiring review remarks.
- Search Functionality: Keyword search by Registration Number, Business Name, or Status.
- Dashboard: Real-time counter widgets showing submission breakdown.
- End-to-End DevOps Toolchain: Jenkinsfile, Selenium UI tests, Docker image build/run, Ansible automation playbooks.

### Out-of-Scope (Future Enhancements)
- Online payment gateway integration (e.g., Stripe, Razorpay) for renewal fees.
- Biometric verification or digital signature cryptographic hardware tokens.
- Live integration with central government corporate registry databases (simulated via local validation).
- Complex multi-agency routing hierarchies (single-tier municipal officer approval is adopted for MVP).

---

## 6. Course Sign-Off & Approvals

| Reviewer Role | Name | Decision | Comments / Conditions | Date |
|---|---|---|---|---|
| **Course Instructor** | *Pending Assignment* | `[ ] APPROVED  [ ] REVISE` | *Awaiting teacher review of MVP boundaries.* | Pending |
| **Student / Author** | Shravni | `SUBMITTED` | *Initial submission for Stage 1 setup.* | 2026-10-04 |
