package com.gov.licence.workflow;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
@WebListener
public final class WorkflowContext implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event){event.getServletContext().setAttribute("licenceService",new LicenceService(Database.local()));}
    public static LicenceService service(ServletContext context){return (LicenceService)context.getAttribute("licenceService");}
}
