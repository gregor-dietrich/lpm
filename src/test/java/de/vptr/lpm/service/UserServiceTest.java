package de.vptr.lpm.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.vptr.lpm.enums.UserStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Integration tests for UserService.
 */
@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Test
    void testCreateUser() {
        final var result = this.userService.createUser(
                "testuser",
                "test@example.com",
                "password123",
                "Test User");

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        assertEquals("Test User", result.displayName());
        assertEquals(UserStatus.ACTIVE, result.status());
    }

    @Test
    void testCreateUserWithDuplicateUsername() {
        this.userService.createUser("testuser1", "test1@example.com", "password123", "Test User 1");
        assertThrows(IllegalArgumentException.class, () -> {
            this.userService.createUser("testuser1", "test2@example.com", "password456", "Test User 2");
        });
    }

    @Test
    void testFindByUsername() {
        this.userService.createUser("findtest", "findtest@example.com", "password123", "Find Test User");
        final var result = this.userService.findByUsername("findtest");
        assertTrue(result.isPresent());
        assertEquals("findtest", result.get().username());
    }

    @Test
    void testAuthenticateValidCredentials() {
        this.userService.createUser("authtest", "authtest@example.com", "password123", "Auth Test User");
        final var result = this.userService.authenticate("authtest", "password123");
        assertTrue(result.isPresent());
        assertEquals("authtest", result.get().username());
    }

    @Test
    void testAuthenticateInvalidPassword() {
        this.userService.createUser("wrongpass", "wrongpass@example.com", "password123", "Wrong Pass User");
        final var result = this.userService.authenticate("wrongpass", "wrongpassword");
        assertTrue(result.isEmpty());
    }

    @Test
    void testCountUsers() {
        final var countBefore = this.userService.countUsers();
        this.userService.createUser("counttest", "counttest@example.com", "password123", "Count Test User");
        final var countAfter = this.userService.countUsers();
        assertEquals(countBefore + 1, countAfter);
    }
}
