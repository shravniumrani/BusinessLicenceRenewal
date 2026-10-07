package com.gov.licence.workflow;
import com.gov.licence.model.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
@WebServlet(urlPatterns={"/owner/renew","/owner/records/view","/owner/records/edit","/owner/records/delete","/officer/records/review","/officer/records/start","/officer/records/decide"})
public final class ApplicationServlet extends HttpServlet {
    private static final List<String> FIELDS=List.of("businessName","registrationNumber","tradeCategory","annualTurnover","expiryDate","contactEmail","contactPhone");
    private User user(HttpServletRequest r){return (User)r.getSession(false).getAttribute("currentUser");}
    private LicenceService service(){return WorkflowContext.service(getServletContext());}
    private long id(HttpServletRequest r){try{long id=Long.parseLong(r.getParameter("id"));if(id<1)throw new NumberFormatException();return id;}catch(NumberFormatException ex){throw new WorkflowException(400,"Invalid application ID.");}}
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse response)throws ServletException,IOException {
        try {
            String path=r.getServletPath();
            if(path.equals("/owner/renew")){r.setAttribute("form",Map.of());form(r,response);return;}
            if(path.endsWith("/delete")||path.endsWith("/start")||path.endsWith("/decide")){response.sendError(405);return;}
            LicenceApplication app=service().get(user(r),id(r));r.setAttribute("application",app);
            if(path.equals("/owner/records/edit")) {
                if(!app.isEditable())throw new WorkflowException(409,"Only submitted applications can be edited.");
                r.setAttribute("form",Map.of("businessName",app.getBusinessName(),"registrationNumber",app.getRegistrationNumber(),"tradeCategory",app.getTradeCategory(),"annualTurnover",app.getAnnualTurnover().toPlainString(),"expiryDate",app.getExpiryDate().toString(),"contactEmail",app.getContactEmail(),"contactPhone",app.getContactPhone()));
                r.setAttribute("editing",true);form(r,response);
            }else {r.setAttribute("owner",user(r).getRole()==Role.BUSINESS_OWNER);detail(r,response);}
        }catch(WorkflowException ex){response.sendError(ex.getStatus(),ex.getMessage());}
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse response)throws ServletException,IOException {
        r.setCharacterEncoding("UTF-8");String path=r.getServletPath();
        Map<String,String> fields=new LinkedHashMap<>(); for(String key:FIELDS)fields.put(key,Objects.toString(r.getParameter(key),""));
        try {
            long recordId;
            switch(path) {
                case "/owner/renew" -> recordId=service().create(user(r),fields);
                case "/owner/records/edit" -> {recordId=id(r);service().update(user(r),recordId,fields);}
                case "/owner/records/delete" -> {service().delete(user(r),id(r));response.sendRedirect(r.getContextPath()+"/owner/dashboard?result=deleted");return;}
                case "/officer/records/start" -> {recordId=id(r);service().startReview(user(r),recordId);}
                case "/officer/records/decide" -> {recordId=id(r);service().decide(user(r),recordId,r.getParameter("decision"),r.getParameter("remarks"));}
                default -> {response.sendError(405);return;}
            }
            response.sendRedirect(r.getContextPath()+(user(r).getRole()==Role.BUSINESS_OWNER?"/owner/records/view":"/officer/records/review")+"?id="+recordId+"&result=saved");
        }catch(WorkflowException ex){
            if(ex.getStatus()==400 || ex.getStatus()==409) {
                response.setStatus(ex.getStatus());r.setAttribute("error",ex.getMessage());
                if(path.equals("/owner/renew") || path.equals("/owner/records/edit")) {
                    r.setAttribute("form",fields);r.setAttribute("editing",path.endsWith("/edit"));form(r,response);
                }else if(path.startsWith("/officer/")) {
                    try{r.setAttribute("application",service().get(user(r),id(r)));r.setAttribute("owner",false);detail(r,response);}
                    catch(WorkflowException missing){response.sendError(missing.getStatus(),missing.getMessage());}
                }else response.sendError(ex.getStatus(),ex.getMessage());
            }else response.sendError(ex.getStatus(),ex.getMessage());
        }
    }
    private void form(HttpServletRequest r,HttpServletResponse response)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/views/application-form.jsp").forward(r,response);}
    private void detail(HttpServletRequest r,HttpServletResponse response)throws ServletException,IOException{r.getRequestDispatcher("/WEB-INF/views/application-detail.jsp").forward(r,response);}
}
