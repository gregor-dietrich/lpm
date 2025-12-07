package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.ProjectDto;
import de.vptr.lpm.entity.Project;
import de.vptr.lpm.repository.ProjectRepository;
import de.vptr.lpm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing projects.
 */
@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository repository;

    @Inject
    UserRepository userRepository;

    /**
     * Create a new project.
     *
     * @param name        project name
     * @param projectKey  unique project key
     * @param description project description
     * @param ownerId     ID of the project owner
     * @return the created project DTO
     */
    @Transactional
    public ProjectDto createProject(
            final String name,
            final String projectKey,
            final String description,
            final Long ownerId) {
        // Check for duplicate key
        if (this.repository.findByKey(projectKey) != null) {
            throw new IllegalArgumentException("Project key '" + projectKey + "' already exists");
        }

        final var owner = this.userRepository.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Owner not found: " + ownerId);
        }

        final var project = new Project();
        project.name = name;
        project.projectKey = projectKey;
        project.description = description;
        project.owner = owner;

        this.repository.persist(project);
        return ProjectDto.fromEntity(project);
    }

    /**
     * Find a project by ID.
     *
     * @param id the project ID
     * @return an Optional containing the project if found
     */
    public Optional<ProjectDto> findById(final Long id) {
        return this.repository.findByIdOptional(id)
                .map(ProjectDto::fromEntity);
    }

    /**
     * Find a project by key.
     *
     * @param projectKey the project key
     * @return an Optional containing the project if found
     */
    public Optional<ProjectDto> findByKey(final String projectKey) {
        final var project = this.repository.findByKey(projectKey);
        return Optional.ofNullable(project)
                .map(ProjectDto::fromEntity);
    }

    /**
     * Get all projects.
     *
     * @return list of all projects
     */
    public List<ProjectDto> listAll() {
        return this.repository.listAll().stream()
                .map(ProjectDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get projects owned by a user.
     *
     * @param userId the user ID
     * @return list of projects owned by the user
     */
    public List<ProjectDto> findByOwner(final Long userId) {
        return this.repository.findByOwner(userId).stream()
                .map(ProjectDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Count all projects.
     *
     * @return total count of projects
     */
    public long countProjects() {
        return this.repository.count();
    }

    /**
     * Update a project.
     *
     * @param id          project ID
     * @param name        new name
     * @param description new description
     * @return the updated project DTO
     */
    @Transactional
    public ProjectDto updateProject(
            final Long id,
            final String name,
            final String description) {
        final var project = this.repository.findById(id);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + id);
        }

        project.name = name;
        project.description = description;

        this.repository.persist(project);
        return ProjectDto.fromEntity(project);
    }

    /**
     * Delete a project.
     *
     * @param id the project ID
     */
    @Transactional
    public void deleteProject(final Long id) {
        this.repository.deleteById(id);
    }
}
