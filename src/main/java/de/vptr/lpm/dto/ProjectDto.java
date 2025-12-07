package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.Project;

/**
 * Data transfer object for Project entity.
 *
 * @param id          unique identifier
 * @param name        project name
 * @param projectKey  unique project key
 * @param description project description
 * @param ownerId     ID of the project owner
 * @param ownerName   display name of the project owner
 * @param statusId    ID of the project status
 * @param statusName  name of the project status
 * @param memberCount number of project members
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record ProjectDto(
        Long id,
        String name,
        String projectKey,
        String description,
        Long ownerId,
        String ownerName,
        Long statusId,
        String statusName,
        Integer memberCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a ProjectDTO from a Project entity.
     *
     * @param project the project entity
     * @return a ProjectDTO with the project's data
     */
    public static ProjectDto fromEntity(final Project project) {
        return new ProjectDto(
                project.id,
                project.name,
                project.projectKey,
                project.description,
                project.owner != null ? project.owner.id : null,
                project.owner != null ? project.owner.displayName : null,
                project.status != null ? project.status.id : null,
                project.status != null ? project.status.name : null,
                project.members != null ? project.members.size() : 0,
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    /**
     * Converts a ProjectDTO to a Project entity. Note: does not set owner or
     * status.
     *
     * @return a new Project entity with data from this DTO
     */
    public Project toEntity() {
        final var project = new Project();
        project.id = this.id;
        project.name = this.name;
        project.projectKey = this.projectKey;
        project.description = this.description;
        return project;
    }
}
