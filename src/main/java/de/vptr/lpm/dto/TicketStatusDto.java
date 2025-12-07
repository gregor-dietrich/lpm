package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.TicketStatus;

/**
 * Data transfer object for TicketStatus entity.
 *
 * @param id          unique identifier
 * @param name        status name
 * @param description status description
 * @param color       color code for UI
 * @param icon        icon name for UI
 * @param order       sort order
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record TicketStatusDto(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        Integer order,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a TicketStatusDTO from a TicketStatus entity.
     *
     * @param status the ticket status entity
     * @return a TicketStatusDTO with the status's data
     */
    public static TicketStatusDto fromEntity(final TicketStatus status) {
        return new TicketStatusDto(
                status.id,
                status.name,
                status.description,
                status.color,
                status.icon,
                status.order,
                status.getCreatedAt(),
                status.getUpdatedAt());
    }

    /**
     * Converts a TicketStatusDTO to a TicketStatus entity.
     *
     * @return a new TicketStatus entity with data from this DTO
     */
    public TicketStatus toEntity() {
        final var status = new TicketStatus();
        status.id = this.id;
        status.name = this.name;
        status.description = this.description;
        status.color = this.color;
        status.icon = this.icon;
        status.order = this.order;
        return status;
    }
}
