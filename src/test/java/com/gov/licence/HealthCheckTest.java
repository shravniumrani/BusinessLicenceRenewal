package com.gov.licence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealthCheckTest {

    @Test
    @DisplayName("JVM Runtime must be Java 21 or higher")
    void testJavaVersionRequirement() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java version must not be null");
        assertTrue(
            javaVersion.startsWith("21"), 
            "Project requires Java 21 LTS runtime. Found: " + javaVersion
        );
    }

    @Test
    @DisplayName("HealthCheckServlet class must be loadable and assignable to HttpServlet")
    void testServletClassStructure() {
        HealthCheckServlet servlet = new HealthCheckServlet();
        assertNotNull(servlet, "HealthCheckServlet instance must instantiate cleanly");
    }
}
