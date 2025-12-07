package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a project status in the system. Project statuses are configurable
 * and define the possible states for projects (Active, On Hold, Archived,
 * etc.).
 */
@Entity
@Table(name = "project_status")
public class ProjectStatus extends AbstractEntity {

    /**
     * Unique name of the project status.
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false)
    public String name;

    /**
     * Description of the status.
     */
    @Size(max = 500)
    public String description;

    /**
     * Color code for UI representation (e.g., "#00AA00").
     */
    @Size(max = 7)
    public String color;

    /**
     * Icon name for UI representation (e.g., "play-circle").
     */
    @Size(max = 50)
    public String icon;

    /**
     * Order for sorting statuses in the UI.
     */
    @Column(name = "\"order\"")
    public Integer order;

    /**
     * Find a project status by name.
     *
     * @param name the status name
     * @return an Optional containing the status if found
     */
    public static Optional<ProjectStatus> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * List all project statuses ordered by order field.
     *
     * @return list of all project statuses
     */
    public static java.util.List<ProjectStatus> findAllOrdered() {
        return find("order", "order").list();
    }
}
