package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a ticket type (Bug, Feature, Task, etc). Ticket types are
 * configurable and define the kinds of work items that can be created.
 */
@Entity
@Table(name = "ticket_type")
public class TicketType extends AbstractEntity {

    /**
     * Unique name of the ticket type.
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false)
    public String name;

    /**
     * Description of the ticket type.
     */
    @Size(max = 500)
    public String description;

    /**
     * Icon name for UI representation (e.g., "bug", "star").
     */
    @Size(max = 50)
    public String icon;

    /**
     * Color code for UI representation (e.g., "#FF0000").
     */
    @Size(max = 7)
    public String color;

    /**
     * Find a ticket type by name.
     *
     * @param name the type name
     * @return an Optional containing the type if found
     */
    public static Optional<TicketType> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }
}
