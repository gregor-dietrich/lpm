package de.vptr.lpm.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for PasswordUtil.
 */
class PasswordUtilTest {

    @Test
    void testHashPassword() {
        final var password = "testPassword123";
        final var hash = PasswordUtil.hashPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.contains(":"));
    }

    @Test
    void testHashPasswordWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
    }

    @Test
    void testHashPasswordWithEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
    }

    @Test
    void testVerifyCorrectPassword() {
        final var password = "testPassword123";
        final var hash = PasswordUtil.hashPassword(password);

        assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    void testVerifyIncorrectPassword() {
        final var password = "testPassword123";
        final var hash = PasswordUtil.hashPassword(password);

        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash));
    }

    @Test
    void testVerifyWithNullPassword() {
        final var hash = PasswordUtil.hashPassword("test");
        assertFalse(PasswordUtil.verifyPassword(null, hash));
    }

    @Test
    void testVerifyWithNullHash() {
        assertFalse(PasswordUtil.verifyPassword("test", null));
    }

    @Test
    void testHashingIsConsistent() {
        final var password = "testPassword123";
        final var hash1 = PasswordUtil.hashPassword(password);
        final var hash2 = PasswordUtil.hashPassword(password);

        // Both hashes should verify the same password
        assertTrue(PasswordUtil.verifyPassword(password, hash1));
        assertTrue(PasswordUtil.verifyPassword(password, hash2));
    }
}
