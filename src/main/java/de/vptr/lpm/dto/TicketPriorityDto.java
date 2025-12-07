package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.TicketPriority;

/**
 * Data transfer object for TicketPriority entity.
 *
 * @param id          unique identifier
 * @param name        priority name
 * @param description priority description
 * @param color       color code for UI
 * @param icon        icon name for UI
 * @param order       sort order
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record TicketPriorityDto(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        Integer order,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a TicketPriorityDTO from a TicketPriority entity.
     *
     * @param priority the ticket priority entity
     * @return a TicketPriorityDTO with the priority's data
     */
    public static TicketPriorityDto fromEntity(final TicketPriority priority) {
        return new TicketPriorityDto(
                priority.id,
                priority.name,
                priority.description,
                priority.color,
                priority.icon,
                priority.order,
                priority.getCreatedAt(),
                priority.getUpdatedAt());
    }

    /**
     * Converts a TicketPriorityDTO to a TicketPriority entity.
     *
     * @return a new TicketPriority entity with data from this DTO
     */
    public TicketPriority toEntity() {
        final var priority = new TicketPriority();
        priority.id = this.id;
        priority.name = this.name;
        priority.description = this.description;
        priority.color = this.color;
        priority.icon = this.icon;
        priority.order = this.order;
        return priority;
    }
}
