package de.vptr.lpm.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a project in the system. Projects contain tickets and are
 * organized
 * by team members with different roles.
 */
@Entity
@Table(name = "project")
public class Project extends AbstractEntity {

    /**
     * Project name.
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    public String name;

    /**
     * Unique project key for ticket numbering (e.g., "PROJ").
     */
    @NotNull
    @Size(min = 1, max = 10)
    @Column(unique = true, nullable = false)
    public String projectKey;

    /**
     * Project description.
     */
    public String description;

    /**
     * Project owner (the user who created it).
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;

    /**
     * Current project status.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    public ProjectStatus status;

    /**
     * Project members with their roles.
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ProjectMember> members = new HashSet<>();

    /**
     * Find a project by key.
     *
     * @param projectKey the project key
     * @return an Optional containing the project if found
     */
    public static Optional<Project> findByKey(final String projectKey) {
        return find("projectKey", projectKey).firstResultOptional();
    }

    /**
     * Find all projects owned by a user.
     *
     * @param userId the user ID
     * @return list of projects owned by the user
     */
    public static java.util.List<Project> findByOwner(final Long userId) {
        return find("owner.id", userId).list();
    }
}
