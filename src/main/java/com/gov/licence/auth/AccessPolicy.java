package com.gov.licence.auth;
import com.gov.licence.model.*;
public final class AccessPolicy {
    private AccessPolicy() { }
    public static boolean permits(User user, String path) {
        if (user == null) return false;
        if (path.equals("/owner") || path.startsWith("/owner/")) return user.getRole() == Role.BUSINESS_OWNER;
        if (path.equals("/officer") || path.startsWith("/officer/")) return user.getRole() == Role.LICENSING_OFFICER;
        return true;
    }
}
