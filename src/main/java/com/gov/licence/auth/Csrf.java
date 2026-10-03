package com.gov.licence.auth;
import jakarta.servlet.http.*;
import java.util.UUID;
public final class Csrf {
    private Csrf() { }
    public static String token(HttpSession session) {
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) { token = UUID.randomUUID().toString(); session.setAttribute("csrfToken", token); }
        return token;
    }
    public static boolean valid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object expected = session.getAttribute("csrfToken");
        return expected != null && expected.equals(request.getParameter("csrfToken"));
    }
}
