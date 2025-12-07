package de.vptr.lpm.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and validation using SHA-256 with salt.
 *
 * This class provides methods to securely hash passwords and verify plain-text
 * passwords against their hashed values.
 */
public final class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int HASH_ITERATIONS = 100000;

    /**
     * Secure random instance for generating salts.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Private constructor to prevent instantiation.
     */
    private PasswordUtil() {
        // Utility class
    }

    /**
     * Hashes a plain-text password using SHA-256 with a random salt.
     *
     * @param password the plain-text password to hash
     * @return the hashed password in format "salt:hash"
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(final String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        final var salt = generateSalt();
        final var hash = hashWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash;
    }

    /**
     * Verifies a plain-text password against a hashed password.
     *
     * @param password       the plain-text password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(final String password, final String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        try {
            final var parts = hashedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }

            final var salt = Base64.getDecoder().decode(parts[0]);
            final var storedHash = parts[1];
            final var computedHash = hashWithSalt(password, salt);

            return constantTimeEquals(storedHash, computedHash);
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Hashes a password with a given salt using SHA-256.
     *
     * @param password the password to hash
     * @param salt     the salt bytes
     * @return the hashed password as a Base64 string
     */
    private static String hashWithSalt(final String password, final byte[] salt) {
        try {
            final var digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(salt);
            var hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            for (int i = 1; i < HASH_ITERATIONS; i++) {
                digest.reset();
                hash = digest.digest(hash);
            }

            return Base64.getEncoder().encodeToString(hash);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found: " + HASH_ALGORITHM, e);
        }
    }

    /**
     * Generates a random salt for password hashing.
     *
     * @return a salt byte array
     */
    private static byte[] generateSalt() {
        final var salt = new byte[SALT_LENGTH];
        PasswordUtil.RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Performs a constant-time string comparison to prevent timing attacks.
     *
     * @param a the first string
     * @param b the second string
     * @return true if the strings are equal, false otherwise
     */
    private static boolean constantTimeEquals(final String a, final String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        final byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        final byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        int result = aBytes.length ^ bBytes.length;
        for (int i = 0; i < Math.min(aBytes.length, bBytes.length); i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }
}