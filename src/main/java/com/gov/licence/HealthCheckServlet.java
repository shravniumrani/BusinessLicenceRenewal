package com.gov.licence;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

/**
 * Health check endpoint for system monitoring, container liveness checks,
 * and Ansible post-deployment verification.
 * Exposed at /health.
 */
@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/health"})
public class HealthCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String APP_VERSION = "0.1.0-SNAPSHOT";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String osName = System.getProperty("os.name");

        try (PrintWriter out = response.getWriter()) {
            out.print("{\n");
            out.print("  \"status\": \"UP\",\n");
            out.print("  \"service\": \"Business Licence Renewal System\",\n");
            out.print("  \"version\": \"" + APP_VERSION + "\",\n");
            out.print("  \"timestamp\": \"" + Instant.now().toString() + "\",\n");
            out.print("  \"javaVersion\": \"" + javaVersion + "\",\n");
            out.print("  \"javaVendor\": \"" + javaVendor + "\",\n");
            out.print("  \"os\": \"" + osName + "\"\n");
            out.print("}");
            out.flush();
        }
    }
}
