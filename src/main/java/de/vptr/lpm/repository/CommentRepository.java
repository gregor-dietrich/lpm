package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.Comment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for Comment entity with custom query methods.
 */
@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {

    /**
     * Find all comments on a ticket.
     *
     * @param ticketId the ticket ID
     * @return list of comments on the ticket
     */
    public List<Comment> findByTicket(final Long ticketId) {
        return this.find("ticket.id = ?1 order by createdAt desc", ticketId).list();
    }

    /**
     * Find all comments by an author.
     *
     * @param userId the user ID
     * @return list of comments by the user
     */
    public List<Comment> findByAuthor(final Long userId) {
        return this.find("author.id = ?1", userId).list();
    }
}
