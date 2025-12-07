package de.vptr.lpm.repository;

import de.vptr.lpm.entity.TicketType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for TicketType entity with custom query methods.
 */
@ApplicationScoped
public class TicketTypeRepository implements PanacheRepository<TicketType> {
}
