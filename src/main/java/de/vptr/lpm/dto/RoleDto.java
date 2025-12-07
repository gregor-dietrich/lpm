package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.Role;

/**
 * Data transfer object for Role entity.
 *
 * @param id          unique identifier
 * @param name        unique role name
 * @param description role description
 * @param color       UI color code
 * @param icon        UI icon name
 * @param order       sort order
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record RoleDto(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        Integer order,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a RoleDTO from a Role entity.
     *
     * @param role the role entity
     * @return a RoleDTO with the role's data
     */
    public static RoleDto fromEntity(final Role role) {
        return new RoleDto(
                role.id,
                role.name,
                role.description,
                role.color,
                role.icon,
                role.order,
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    /**
     * Converts a RoleDTO to a Role entity.
     *
     * @return a new Role entity with data from this DTO
     */
    public Role toEntity() {
        final var role = new Role();
        role.id = this.id;
        role.name = this.name;
        role.description = this.description;
        role.color = this.color;
        role.icon = this.icon;
        role.order = this.order;
        return role;
    }
}
