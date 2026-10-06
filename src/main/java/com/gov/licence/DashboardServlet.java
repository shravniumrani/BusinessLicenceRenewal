package com.gov.licence;
import com.gov.licence.model.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
@WebServlet(urlPatterns = {"/dashboard", "/owner/dashboard", "/officer/dashboard"})
public final class DashboardServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession(false).getAttribute("currentUser");
        if (request.getServletPath().equals("/dashboard")) {
            response.sendRedirect(request.getContextPath() + (user.getRole() == Role.BUSINESS_OWNER ? "/owner/dashboard" : "/officer/dashboard"));
            return;
        }
        request.setAttribute("roleLabel", user.getRole() == Role.BUSINESS_OWNER ? "Business owner" : "Licensing officer");
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
