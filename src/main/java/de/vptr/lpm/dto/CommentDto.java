package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.Comment;

/**
 * Data transfer object for Comment entity.
 *
 * @param id         unique identifier
 * @param ticketId   ID of the ticket
 * @param authorId   ID of the comment author
 * @param authorName display name of the author
 * @param content    comment text
 * @param createdAt  creation timestamp
 * @param updatedAt  last update timestamp
 */
public record CommentDto(
        Long id,
        Long ticketId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a CommentDTO from a Comment entity.
     *
     * @param comment the comment entity
     * @return a CommentDTO with the comment's data
     */
    public static CommentDto fromEntity(final Comment comment) {
        return new CommentDto(
                comment.id,
                comment.ticket != null ? comment.ticket.id : null,
                comment.author != null ? comment.author.id : null,
                comment.author != null ? comment.author.displayName : null,
                comment.content,
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    /**
     * Converts a CommentDTO to a Comment entity. Note: does not set ticket or
     * author.
     *
     * @return a new Comment entity with data from this DTO
     */
    public Comment toEntity() {
        final var comment = new Comment();
        comment.id = this.id;
        comment.content = this.content;
        return comment;
    }
}
