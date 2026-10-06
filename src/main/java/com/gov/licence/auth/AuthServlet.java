package com.gov.licence.auth;
import com.gov.licence.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
@WebServlet(urlPatterns = {"/login", "/logout"})
public final class AuthServlet extends HttpServlet {
    private final UserService users = new UserService();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        if (request.getServletPath().equals("/logout")) { response.sendError(405); return; }
        HttpSession session = request.getSession();
        if (session.getAttribute("currentUser") != null) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
        Csrf.token(session);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        if (!Csrf.valid(request)) { response.sendError(403, "Invalid form token"); return; }
        if (request.getServletPath().equals("/logout")) {
            request.getSession(false).invalidate();
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }
        HttpSession session = request.getSession();
        Long lockedUntil = (Long) session.getAttribute("loginLockedUntil");
        if (lockedUntil != null && lockedUntil > System.currentTimeMillis()) {
            response.setStatus(429); request.setAttribute("error", "Too many attempts. Wait one minute and try again.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response); return;
        }
        String submitted = request.getParameter("password");
        char[] password = submitted == null ? new char[0] : submitted.toCharArray();
        Optional<User> user;
        try { user = users.authenticate(request.getParameter("username"), password); }
        finally { Arrays.fill(password, '\0'); }
        if (user.isEmpty()) {
            Integer attempts = (Integer) session.getAttribute("loginAttempts");
            attempts = attempts == null || lockedUntil != null ? 1 : attempts + 1;
            session.removeAttribute("loginLockedUntil"); session.setAttribute("loginAttempts", attempts);
            if (attempts >= 5) { session.setAttribute("loginLockedUntil", System.currentTimeMillis() + 60000); session.setAttribute("loginAttempts", 0); }
            response.setStatus(401); request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response); return;
        }
        request.changeSessionId();
        session.setAttribute("currentUser", user.get());
        session.removeAttribute("loginAttempts"); session.removeAttribute("loginLockedUntil");
        session.removeAttribute("csrfToken"); Csrf.token(session);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
