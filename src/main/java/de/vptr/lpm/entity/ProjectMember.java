package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a member of a project with a specific role within that project.
 */
@Entity
@Table(name = "project_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "project_id", "user_id" })
})
public class ProjectMember extends AbstractEntity {

    /**
     * The project this member belongs to.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    /**
     * The user who is a member of the project.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    /**
     * The member's role within this project (e.g., "LEAD", "DEVELOPER", "VIEWER").
     */
    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    public String role;

    /**
     * Find a project member by project and user.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     * @return an Optional containing the project member if found
     */
    public static Optional<ProjectMember> findByProjectAndUser(final Long projectId, final Long userId) {
        return find("project.id = ?1 and user.id = ?2", projectId, userId).firstResultOptional();
    }

    /**
     * Find all members of a project.
     *
     * @param projectId the project ID
     * @return list of project members
     */
    public static java.util.List<ProjectMember> findByProject(final Long projectId) {
        return find("project.id", projectId).list();
    }
}
