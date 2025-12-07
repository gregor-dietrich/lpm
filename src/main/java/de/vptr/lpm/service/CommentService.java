package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.CommentDto;
import de.vptr.lpm.entity.Comment;
import de.vptr.lpm.repository.CommentRepository;
import de.vptr.lpm.repository.TicketRepository;
import de.vptr.lpm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing comments on tickets.
 */
@ApplicationScoped
public class CommentService {

    @Inject
    CommentRepository repository;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ActivityLogService activityLogService;

    /**
     * Create a new comment on a ticket.
     *
     * @param ticketId the ticket ID
     * @param authorId the author user ID
     * @param content  the comment content
     * @return the created comment DTO
     */
    @Transactional
    public CommentDto createComment(
            final Long ticketId,
            final Long authorId,
            final String content) {
        final var ticket = this.ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }

        final var author = this.userRepository.findById(authorId);
        if (author == null) {
            throw new IllegalArgumentException("Author not found: " + authorId);
        }

        final var comment = new Comment();
        comment.ticket = ticket;
        comment.author = author;
        comment.content = content;

        this.repository.persist(comment);

        // Log activity
        this.activityLogService.logActivity("Comment", comment.id, "CREATE", authorId, null);

        return CommentDto.fromEntity(comment);
    }

    /**
     * Find a comment by ID.
     *
     * @param id the comment ID
     * @return an Optional containing the comment if found
     */
    public Optional<CommentDto> findById(final Long id) {
        return this.repository.findByIdOptional(id)
                .map(CommentDto::fromEntity);
    }

    /**
     * Get all comments on a ticket.
     *
     * @param ticketId the ticket ID
     * @return list of comments
     */
    public List<CommentDto> findByTicket(final Long ticketId) {
        return this.repository.findByTicket(ticketId).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all comments by an author.
     *
     * @param userId the user ID
     * @return list of comments
     */
    public List<CommentDto> findByAuthor(final Long userId) {
        return this.repository.findByAuthor(userId).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update a comment.
     *
     * @param id      comment ID
     * @param content new content
     * @return the updated comment DTO
     */
    @Transactional
    public CommentDto updateComment(final Long id, final String content) {
        final var comment = this.repository.findById(id);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found: " + id);
        }

        comment.content = content;
        this.repository.persist(comment);

        return CommentDto.fromEntity(comment);
    }

    /**
     * Delete a comment.
     *
     * @param id the comment ID
     */
    @Transactional
    public void deleteComment(final Long id) {
        this.repository.deleteById(id);
    }

    /**
     * Count comments on a ticket.
     *
     * @param ticketId the ticket ID
     * @return total count of comments
     */
    public long countByTicket(final Long ticketId) {
        return this.repository.count("ticket.id = ?1", ticketId);
    }
}
