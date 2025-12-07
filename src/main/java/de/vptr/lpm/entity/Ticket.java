package de.vptr.lpm.entity;

import java.util.Optional;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a ticket (issue/task) within a project. Tickets are the main work
 * items
 * tracked in the system with types, priorities, statuses, and assignments.
 */
@Entity
@Table(name = "ticket")
public class Ticket extends AbstractEntity {

    /**
     * The project this ticket belongs to.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    public Project project;

    /**
     * Unique ticket key within the project (e.g., "PROJ-123").
     */
    @NotNull
    @Size(min = 1, max = 20)
    @Column(unique = true, nullable = false)
    public String ticketKey;

    /**
     * Ticket title/summary.
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    public String title;

    /**
     * Detailed description of the ticket.
     */
    public String description;

    /**
     * The type of ticket (Bug, Feature, Task, etc).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    public TicketType type;

    /**
     * The current status of the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    public TicketStatus status;

    /**
     * The priority level of the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id")
    public TicketPriority priority;

    /**
     * The user assigned to work on this ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    public User assignee;

    /**
     * The user who created this ticket.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    public User reporter;

    /**
     * Find a ticket by key.
     *
     * @param ticketKey the ticket key
     * @return an Optional containing the ticket if found
     */
    public static Optional<Ticket> findByKey(final String ticketKey) {
        return find("ticketKey", ticketKey).firstResultOptional();
    }

    /**
     * Find all tickets in a project.
     *
     * @param projectId the project ID
     * @return list of tickets in the project
     */
    public static java.util.List<Ticket> findByProject(final Long projectId) {
        return find("project.id", projectId).list();
    }

    /**
     * Find all tickets assigned to a user.
     *
     * @param userId the user ID
     * @return list of assigned tickets
     */
    public static java.util.List<Ticket> findByAssignee(final Long userId) {
        return find("assignee.id", userId).list();
    }
}
