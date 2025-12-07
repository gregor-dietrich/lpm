package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.TicketStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for TicketStatus entity with custom query methods.
 */
@ApplicationScoped
public class TicketStatusRepository implements PanacheRepository<TicketStatus> {

    /**
     * Find all ticket statuses ordered by order field.
     *
     * @return list of all ticket statuses
     */
    public List<TicketStatus> findAllOrdered() {
        return this.find("ORDER BY order").list();
    }
}
