package com.gov.licence.auth;
import com.gov.licence.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
@WebFilter(urlPatterns = {"/dashboard", "/owner/*", "/officer/*", "/logout"})
public final class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("currentUser");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        if (!AccessPolicy.permits(user, request.getServletPath())) { response.sendError(403); return; }
        if (!request.getMethod().equals("GET") && !request.getMethod().equals("HEAD") && !Csrf.valid(request)) {
            response.sendError(403, "Invalid form token"); return;
        }
        chain.doFilter(req, res);
    }
}
