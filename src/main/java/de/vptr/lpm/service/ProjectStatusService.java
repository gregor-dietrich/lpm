package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.ProjectStatusDto;
import de.vptr.lpm.entity.ProjectStatus;
import de.vptr.lpm.repository.ProjectStatusRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing project statuses.
 */
@ApplicationScoped
public class ProjectStatusService {

    @Inject
    ProjectStatusRepository repository;

    /**
     * Create a new project status.
     *
     * @param name        status name
     * @param description status description
     * @param color       color code
     * @param icon        icon name
     * @param order       sort order
     * @return the created project status DTO
     */
    @Transactional
    public ProjectStatusDto createStatus(
            final String name,
            final String description,
            final String color,
            final String icon,
            final Integer order) {
        // Check for duplicates
        if (this.repository.find("name", name).firstResult() != null) {
            throw new IllegalArgumentException("Project status '" + name + "' already exists");
        }

        final var status = new ProjectStatus();
        status.name = name;
        status.description = description;
        status.color = color;
        status.icon = icon;
        status.order = order;

        this.repository.persist(status);
        return ProjectStatusDto.fromEntity(status);
    }

    /**
     * Find a project status by ID.
     *
     * @param id the project status ID
     * @return an Optional containing the status if found
     */
    public Optional<ProjectStatusDto> findById(final Long id) {
        return this.repository.findByIdOptional(id)
                .map(ProjectStatusDto::fromEntity);
    }

    /**
     * Find a project status by name.
     *
     * @param name the status name
     * @return an Optional containing the status if found
     */
    public Optional<ProjectStatusDto> findByName(final String name) {
        return this.repository.find("name", name).firstResultOptional()
                .map(ProjectStatusDto::fromEntity);
    }

    /**
     * Get all project statuses ordered by order field.
     *
     * @return list of all project statuses
     */
    public List<ProjectStatusDto> listAll() {
        return this.repository.findAllOrdered().stream()
                .map(ProjectStatusDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Count all project statuses.
     *
     * @return total count of project statuses
     */
    public long countStatuses() {
        return this.repository.count();
    }

    /**
     * Update a project status.
     *
     * @param id          status ID
     * @param name        new name
     * @param description new description
     * @param color       new color
     * @param icon        new icon
     * @param order       new order
     * @return the updated status DTO
     */
    @Transactional
    public ProjectStatusDto updateStatus(
            final Long id,
            final String name,
            final String description,
            final String color,
            final String icon,
            final Integer order) {
        final var status = this.repository.findById(id);
        if (status == null) {
            throw new IllegalArgumentException("Project status not found: " + id);
        }

        // Check for name conflicts
        if (!status.name.equals(name) && this.repository.find("name", name).firstResult() != null) {
            throw new IllegalArgumentException("Project status '" + name + "' already exists");
        }

        status.name = name;
        status.description = description;
        status.color = color;
        status.icon = icon;
        status.order = order;

        this.repository.persist(status);
        return ProjectStatusDto.fromEntity(status);
    }

    /**
     * Delete a project status.
     *
     * @param id the status ID
     */
    @Transactional
    public void deleteStatus(final Long id) {
        this.repository.deleteById(id);
    }
}
