package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a configurable role in the system. Roles can be created, modified,
 * and deleted by administrators.
 */
@Entity
@Table(name = "role")
public class Role extends AbstractEntity {

    /**
     * Unique name of the role.
     */
    @NotNull
    @Size(min = 1, max = 50)
    @Column(unique = true, nullable = false)
    public String name;

    /**
     * Description of the role's purpose.
     */
    @Size(max = 500)
    public String description;

    /**
     * Color code for UI representation (e.g., "#FF5733").
     */
    @Size(max = 7)
    public String color;

    /**
     * Icon name for UI representation (e.g., "admin", "manager").
     */
    @Size(max = 50)
    public String icon;

    /**
     * Order for sorting roles in the UI.
     */
    @Column(name = "\"order\"")
    public Integer order;

    /**
     * Find a role by name.
     *
     * @param name the role name
     * @return an Optional containing the role if found
     */
    public static Optional<Role> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }
}
