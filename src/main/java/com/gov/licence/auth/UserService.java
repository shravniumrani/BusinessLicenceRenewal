package com.gov.licence.auth;
import com.gov.licence.model.*;
import java.io.*;
import java.util.*;
/** Demo credential repository; replace with a user DAO when registration is added. */
public final class UserService {
    private final Properties credentials = new Properties();
    public UserService() {
        try (InputStream in = UserService.class.getResourceAsStream("/demo-users.properties")) {
            if (in == null) throw new IllegalStateException("Demo credential resource missing");
            credentials.load(in);
        } catch (IOException ex) { throw new IllegalStateException("Cannot load demo users", ex); }
    }
    public Optional<User> authenticate(String username, char[] password) {
        String name = username == null ? "" : username.trim();
        if (name.length() > 50 || password == null || password.length > 128) return Optional.empty();
        // Unknown accounts perform the same hash work to reduce timing differences.
        String hash = credentials.getProperty(name + ".password", credentials.getProperty("owner1.password"));
        boolean valid = PasswordHasher.verify(password, hash);
        if (!valid || !credentials.containsKey(name + ".id")) return Optional.empty();
        return Optional.of(new User(Long.parseLong(credentials.getProperty(name + ".id")), name,
                credentials.getProperty(name + ".name"), Role.valueOf(credentials.getProperty(name + ".role"))));
    }
}
