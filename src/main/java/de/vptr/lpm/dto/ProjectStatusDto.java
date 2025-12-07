package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.ProjectStatus;

/**
 * Data transfer object for ProjectStatus entity.
 *
 * @param id          unique identifier
 * @param name        status name
 * @param description status description
 * @param color       color code for UI
 * @param icon        icon name for UI
 * @param order       sort order
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record ProjectStatusDto(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        Integer order,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a ProjectStatusDTO from a ProjectStatus entity.
     *
     * @param status the project status entity
     * @return a ProjectStatusDTO with the status's data
     */
    public static ProjectStatusDto fromEntity(final ProjectStatus status) {
        return new ProjectStatusDto(
                status.id,
                status.name,
                status.description,
                status.color,
                status.icon,
                status.order,
                status.getCreatedAt(),
                status.getUpdatedAt());
    }

    /**
     * Converts a ProjectStatusDTO to a ProjectStatus entity.
     *
     * @return a new ProjectStatus entity with data from this DTO
     */
    public ProjectStatus toEntity() {
        final var status = new ProjectStatus();
        status.id = this.id;
        status.name = this.name;
        status.description = this.description;
        status.color = this.color;
        status.icon = this.icon;
        status.order = this.order;
        return status;
    }
}
