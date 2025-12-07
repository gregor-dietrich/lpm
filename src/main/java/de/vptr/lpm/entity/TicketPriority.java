package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a ticket priority level (Lowest to Highest). Ticket priorities are
 * configurable and define the urgency of work items.
 */
@Entity
@Table(name = "ticket_priority")
public class TicketPriority extends AbstractEntity {

    /**
     * Unique name of the priority level.
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false)
    public String name;

    /**
     * Description of the priority level.
     */
    @Size(max = 500)
    public String description;

    /**
     * Color code for UI representation (e.g., "#FF0000").
     */
    @Size(max = 7)
    public String color;

    /**
     * Icon name for UI representation (e.g., "arrow-up").
     */
    @Size(max = 50)
    public String icon;

    /**
     * Order for sorting priorities in the UI.
     */
    @Column(name = "\"order\"")
    public Integer order;

    /**
     * Find a ticket priority by name.
     *
     * @param name the priority name
     * @return an Optional containing the priority if found
     */
    public static Optional<TicketPriority> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * List all ticket priorities ordered by order field.
     *
     * @return list of all ticket priorities
     */
    public static java.util.List<TicketPriority> findAllOrdered() {
        return find("ORDER BY order").list();
    }
}
