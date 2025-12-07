package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.TicketPriority;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for TicketPriority entity with custom query methods.
 */
@ApplicationScoped
public class TicketPriorityRepository implements PanacheRepository<TicketPriority> {

    /**
     * Find all ticket priorities ordered by order field.
     *
     * @return list of all ticket priorities
     */
    public List<TicketPriority> findAllOrdered() {
        return this.find("ORDER BY order").list();
    }
}
