package com.gov.licence.auth;
import com.gov.licence.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AuthTest {
    private final UserService users = new UserService();
    @Test void ownerLoginUsesStoredRole() {
        User user = users.authenticate("owner1", "OwnerDemo!2026".toCharArray()).orElseThrow();
        assertEquals(Role.BUSINESS_OWNER, user.getRole()); assertEquals(1, user.getId());
    }
    @Test void officerLoginUsesStoredRole() {
        assertEquals(Role.LICENSING_OFFICER, users.authenticate("officer", "OfficerDemo!2026".toCharArray()).orElseThrow().getRole());
    }
    @Test void incorrectCredentialsAreRejected() {
        assertTrue(users.authenticate("owner1", "wrong".toCharArray()).isEmpty());
        assertTrue(users.authenticate("unknown", "OwnerDemo!2026".toCharArray()).isEmpty());
        assertTrue(users.authenticate(null, null).isEmpty());
    }
    @Test void malformedHashesAreRejected() { assertFalse(PasswordHasher.verify("password".toCharArray(), "broken")); }
    @Test void rolesCannotCrossWorkspaces() {
        User owner = new User(1, "owner1", "Owner", Role.BUSINESS_OWNER);
        User officer = new User(3, "officer", "Officer", Role.LICENSING_OFFICER);
        assertTrue(AccessPolicy.permits(owner, "/owner/dashboard"));
        assertFalse(AccessPolicy.permits(owner, "/officer/dashboard"));
        assertTrue(AccessPolicy.permits(officer, "/officer/dashboard"));
        assertFalse(AccessPolicy.permits(officer, "/owner/dashboard"));
        assertFalse(AccessPolicy.permits(null, "/dashboard"));
    }
}
