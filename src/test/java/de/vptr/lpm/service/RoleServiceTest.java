package de.vptr.lpm.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Integration tests for RoleService.
 */
@QuarkusTest
class RoleServiceTest {

    @Inject
    RoleService roleService;

    @Test
    void testCreateRole() {
        final var result = this.roleService.createRole(
                "CUSTOM_ROLE",
                "Custom Role",
                "#FF0000",
                "custom",
                10);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("CUSTOM_ROLE", result.name());
        assertEquals("Custom Role", result.description());
        assertEquals("#FF0000", result.color());
        assertEquals("custom", result.icon());
        assertEquals(10, result.order());
    }

    @Test
    void testCreateRoleWithDuplicateName() {
        this.roleService.createRole("ROLE1", "Role 1", "#FF0000", "role1", 1);
        assertThrows(IllegalArgumentException.class, () -> {
            this.roleService.createRole("ROLE1", "Another Role 1", "#00FF00", "other", 2);
        });
    }

    @Test
    void testFindByName() {
        this.roleService.createRole("FINDTEST", "Find Test", "#0000FF", "findtest", 5);
        final var result = this.roleService.findByName("FINDTEST");
        assertTrue(result.isPresent());
        assertEquals("FINDTEST", result.get().name());
    }

    @Test
    void testListAll() {
        final var roles = this.roleService.listAll();
        assertNotNull(roles);
        assertTrue(roles.size() > 0);
    }

    @Test
    void testCountRoles() {
        final var countBefore = this.roleService.countRoles();
        this.roleService.createRole("COUNTTEST", "Count Test", "#FFFF00", "counttest", 99);
        final var countAfter = this.roleService.countRoles();
        assertEquals(countBefore + 1, countAfter);
    }
}
