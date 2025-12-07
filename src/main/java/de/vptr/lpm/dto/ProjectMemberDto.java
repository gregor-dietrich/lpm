package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.ProjectMember;

/**
 * Data transfer object for ProjectMember entity.
 *
 * @param id        unique identifier
 * @param projectId ID of the project
 * @param userId    ID of the user
 * @param userName  display name of the user
 * @param role      member's role in the project
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
public record ProjectMemberDto(
        Long id,
        Long projectId,
        Long userId,
        String userName,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a ProjectMemberDTO from a ProjectMember entity.
     *
     * @param member the project member entity
     * @return a ProjectMemberDTO with the member's data
     */
    public static ProjectMemberDto fromEntity(final ProjectMember member) {
        return new ProjectMemberDto(
                member.id,
                member.project != null ? member.project.id : null,
                member.user != null ? member.user.id : null,
                member.user != null ? member.user.displayName : null,
                member.role,
                member.getCreatedAt(),
                member.getUpdatedAt());
    }

    /**
     * Converts a ProjectMemberDTO to a ProjectMember entity. Note: does not set
     * project or user.
     *
     * @return a new ProjectMember entity with data from this DTO
     */
    public ProjectMember toEntity() {
        final var member = new ProjectMember();
        member.id = this.id;
        member.role = this.role;
        return member;
    }
}
