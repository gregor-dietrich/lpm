package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.Ticket;

/**
 * Data transfer object for Ticket entity.
 *
 * @param id           unique identifier
 * @param projectId    ID of the project
 * @param ticketKey    unique ticket key
 * @param title        ticket title
 * @param description  ticket description
 * @param typeId       ID of the ticket type
 * @param typeName     name of the ticket type
 * @param statusId     ID of the ticket status
 * @param statusName   name of the ticket status
 * @param priorityId   ID of the ticket priority
 * @param priorityName name of the ticket priority
 * @param assigneeId   ID of the assigned user
 * @param assigneeName display name of the assigned user
 * @param reporterId   ID of the reporting user
 * @param reporterName display name of the reporting user
 * @param createdAt    creation timestamp
 * @param updatedAt    last update timestamp
 */
public record TicketDto(
        Long id,
        Long projectId,
        String ticketKey,
        String title,
        String description,
        Long typeId,
        String typeName,
        Long statusId,
        String statusName,
        Long priorityId,
        String priorityName,
        Long assigneeId,
        String assigneeName,
        Long reporterId,
        String reporterName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a TicketDTO from a Ticket entity.
     *
     * @param ticket the ticket entity
     * @return a TicketDTO with the ticket's data
     */
    public static TicketDto fromEntity(final Ticket ticket) {
        return new TicketDto(
                ticket.id,
                ticket.project != null ? ticket.project.id : null,
                ticket.ticketKey,
                ticket.title,
                ticket.description,
                ticket.type != null ? ticket.type.id : null,
                ticket.type != null ? ticket.type.name : null,
                ticket.status != null ? ticket.status.id : null,
                ticket.status != null ? ticket.status.name : null,
                ticket.priority != null ? ticket.priority.id : null,
                ticket.priority != null ? ticket.priority.name : null,
                ticket.assignee != null ? ticket.assignee.id : null,
                ticket.assignee != null ? ticket.assignee.displayName : null,
                ticket.reporter != null ? ticket.reporter.id : null,
                ticket.reporter != null ? ticket.reporter.displayName : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt());
    }

    /**
     * Converts a TicketDTO to a Ticket entity. Note: does not set project, type,
     * status,
     * priority, assignee, or reporter.
     *
     * @return a new Ticket entity with data from this DTO
     */
    public Ticket toEntity() {
        final var ticket = new Ticket();
        ticket.id = this.id;
        ticket.ticketKey = this.ticketKey;
        ticket.title = this.title;
        ticket.description = this.description;
        return ticket;
    }
}
