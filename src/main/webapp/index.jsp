<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Business Licence Renewal System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <p style="text-align:center;padding:16px"><a id="sign-in-link" href="<%= request.getContextPath() %>/login">Sign in to your workspace</a></p>

    <!-- Header Banner -->
    <header class="gov-header">
        <div class="header-container">
            <div class="brand-wrapper">
                <span class="brand-icon">&#127963;</span>
                <div class="brand-title">
                    <h1>Municipal Business Licensing Authority</h1>
                    <p>Business Licence Renewal &amp; Compliance Portal</p>
                </div>
            </div>
            <div class="header-status-badge">
                <span>Stage 1: Initial Skeleton &bull; v0.1.0-SNAPSHOT</span>
            </div>
        </div>
    </header>

    <!-- Main Content Container -->
    <main class="main-content">

        <!-- Scope Approval Banner -->
        <div class="status-banner">
            <div>
                <strong>Scope Approval Status: PENDING TEACHER REVIEW</strong>
                <p>Initial project skeleton deployed for college DevOps assignment evaluation. Core feature branches will be implemented in subsequent stages.</p>
            </div>
            <a href="https://github.com" target="_blank" class="btn btn-outline" style="white-space: nowrap;">Docs Folder</a>
        </div>

        <!-- Hero Section -->
        <section class="hero-card">
            <h2>Business Licence Renewal System</h2>
            <p>
                A secure, automated web platform designed to streamline corporate operating licence renewals,
                enforce server-side role boundaries between Business Owners and Licensing Officers, and demonstrate
                an end-to-end DevOps automation pipeline with Jenkins, Selenium, Docker, and Ansible.
            </p>
        </section>

        <!-- Feature Modules Grid -->
        <div class="grid-container">

            <!-- Card 1: Business Owner Portal -->
            <div class="card">
                <div class="card-header">
                    <h3>&#128188; Business Owner Portal</h3>
                </div>
                <div class="card-body">
                    <span class="badge-tag badge-pending">Stage 2 Deliverable</span>
                    <p>Self-service workspace for registered commercial enterprises.</p>
                    <ul class="feature-list">
                        <li>Submit licence renewal applications</li>
                        <li>Horizontal data isolation (own records only)</li>
                        <li>Real-time application status tracking</li>
                        <li>Update pending drafts prior to audit</li>
                    </ul>
                </div>
            </div>

            <!-- Card 2: Licensing Officer Queue -->
            <div class="card">
                <div class="card-header">
                    <h3>&#9878; Licensing Officer Queue</h3>
                </div>
                <div class="card-body">
                    <span class="badge-tag badge-pending">Stage 2 Deliverable</span>
                    <p>Compliance and review workspace for authorized municipal officers.</p>
                    <ul class="feature-list">
                        <li>Centralized jurisdiction renewal queue</li>
                        <li>Keyword search by registration number</li>
                        <li>Approve or Reject with mandatory remarks</li>
                        <li>End-to-end decision audit logging</li>
                    </ul>
                </div>
            </div>

            <!-- Card 3: Summary Dashboard -->
            <div class="card">
                <div class="card-header">
                    <h3>&#128202; Summary Metrics Dashboard</h3>
                </div>
                <div class="card-body">
                    <span class="badge-tag badge-pending">Stage 2 Deliverable</span>
                    <p>Real-time statistical overview for authority administrators.</p>
                    <ul class="feature-list">
                        <li>Total Renewal Applications Count</li>
                        <li>Pending Review Backlog Counter</li>
                        <li>Approved and Rejected breakdown</li>
                        <li>Performance throughput metrics</li>
                    </ul>
                </div>
            </div>

            <!-- Card 4: System Health & Monitoring -->
            <div class="card">
                <div class="card-header">
                    <h3>&#9881; System Health &amp; Diagnostics</h3>
                </div>
                <div class="card-body">
                    <span class="badge-tag badge-live">Endpoint Active</span>
                    <p>Unauthenticated JSON health check endpoint for monitoring probes.</p>
                    <table class="diag-table">
                        <tr>
                            <th>Java Version</th>
                            <td><%= System.getProperty("java.version") %> (<%= System.getProperty("java.vendor") %>)</td>
                        </tr>
                        <tr>
                            <th>Servlet Specification</th>
                            <td>Jakarta EE 10 / Servlet <%= application.getMajorVersion() %>.<%= application.getMinorVersion() %></td>
                        </tr>
                        <tr>
                            <th>Server Runtime</th>
                            <td><%= application.getServerInfo() %></td>
                        </tr>
                        <tr>
                            <th>Health Endpoint</th>
                            <td><a href="<%= request.getContextPath() %>/health" class="btn btn-outline" style="padding: 0.2rem 0.5rem; font-size: 0.8rem;">GET /health</a></td>
                        </tr>
                    </table>
                </div>
            </div>

        </div>

    </main>

    <!-- Footer -->
    <footer class="gov-footer">
        <div class="footer-container">
            <div>
                &copy; 2026 Municipal Licensing Authority &bull; DevOps Project: Jenkins Deployment for a Business Licence Renewal System
            </div>
            <div class="footer-links">
                <span>Java 21 LTS</span> &bull;
                <span>Apache Tomcat 10.1+</span> &bull;
                <span>Jenkins CI/CD</span> &bull;
                <span>Docker</span> &bull;
                <span>Ansible</span>
            </div>
        </div>
    </footer>

</body>
</html>
