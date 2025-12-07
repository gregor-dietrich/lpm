package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a ticket status (Open, In Progress, Done, etc). Ticket statuses
 * are
 * configurable and define the workflow states for tickets.
 */
@Entity
@Table(name = "ticket_status")
public class TicketStatus extends AbstractEntity {

    /**
     * Unique name of the ticket status.
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(unique = true, nullable = false)
    public String name;

    /**
     * Description of the ticket status.
     */
    @Size(max = 500)
    public String description;

    /**
     * Color code for UI representation (e.g., "#00AA00").
     */
    @Size(max = 7)
    public String color;

    /**
     * Icon name for UI representation (e.g., "check-circle").
     */
    @Size(max = 50)
    public String icon;

    /**
     * Order for sorting statuses in the UI.
     */
    @Column(name = "\"order\"")
    public Integer order;

    /**
     * Find a ticket status by name.
     *
     * @param name the status name
     * @return an Optional containing the status if found
     */
    public static Optional<TicketStatus> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }

    /**
     * List all ticket statuses ordered by order field.
     *
     * @return list of all ticket statuses
     */
    public static java.util.List<TicketStatus> findAllOrdered() {
        return find("ORDER BY order").list();
    }
}
