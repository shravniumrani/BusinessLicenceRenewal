package com.gov.licence.auth;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
/** Stored format: pbkdf2-sha256$iterations$salt$derivedKey. */
public final class PasswordHasher {
    private PasswordHasher() { }
    public static boolean verify(char[] password, String stored) {
        if (password == null || stored == null) return false;
        PBEKeySpec spec = null;
        try {
            String[] parts = stored.split("\\$", -1);
            if (parts.length != 4 || !parts[0].equals("pbkdf2-sha256")) return false;
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            if (iterations < 100000 || iterations > 1000000 || salt.length < 16 || expected.length != 32) return false;
            spec = new PBEKeySpec(password, salt, iterations, 256);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException | GeneralSecurityException ex) {
            return false;
        } finally { if (spec != null) spec.clearPassword(); }
    }
}
