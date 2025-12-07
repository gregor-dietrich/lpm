package de.vptr.lpm.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a comment on a ticket. Comments enable discussion and
 * collaboration
 * on specific work items.
 */
@Entity
@Table(name = "comment")
public class Comment extends AbstractEntity {

    /**
     * The ticket this comment belongs to.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    public Ticket ticket;

    /**
     * The user who authored this comment.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    public User author;

    /**
     * The comment text content.
     */
    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    public String content;

    /**
     * Find all comments on a ticket.
     *
     * @param ticketId the ticket ID
     * @return list of comments on the ticket
     */
    public static List<Comment> findByTicket(final Long ticketId) {
        return find("ticket.id", ticketId).list();
    }

    /**
     * Find all comments by an author.
     *
     * @param userId the user ID
     * @return list of comments by the user
     */
    public static List<Comment> findByAuthor(final Long userId) {
        return find("author.id", userId).list();
    }
}
