package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.Ticket;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for Ticket entity with custom query methods.
 */
@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {

    /**
     * Find all tickets in a project.
     *
     * @param projectId the project ID
     * @return list of tickets in the project
     */
    public List<Ticket> findByProject(final Long projectId) {
        return this.find("project.id = ?1", projectId).list();
    }

    /**
     * Find all tickets assigned to a user.
     *
     * @param userId the user ID
     * @return list of assigned tickets
     */
    public List<Ticket> findByAssignee(final Long userId) {
        return this.find("assignee.id = ?1", userId).list();
    }

    /**
     * Find a ticket by its key.
     *
     * @param ticketKey the ticket key
     * @return the ticket if found, or null
     */
    public Ticket findByKey(final String ticketKey) {
        return this.find("ticketKey", ticketKey).firstResult();
    }
}
