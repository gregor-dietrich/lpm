package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.TicketType;

/**
 * Data transfer object for TicketType entity.
 *
 * @param id          unique identifier
 * @param name        ticket type name
 * @param description type description
 * @param icon        icon name for UI
 * @param color       color code for UI
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record TicketTypeDto(
        Long id,
        String name,
        String description,
        String icon,
        String color,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a TicketTypeDTO from a TicketType entity.
     *
     * @param type the ticket type entity
     * @return a TicketTypeDTO with the type's data
     */
    public static TicketTypeDto fromEntity(final TicketType type) {
        return new TicketTypeDto(
                type.id,
                type.name,
                type.description,
                type.icon,
                type.color,
                type.getCreatedAt(),
                type.getUpdatedAt());
    }

    /**
     * Converts a TicketTypeDTO to a TicketType entity.
     *
     * @return a new TicketType entity with data from this DTO
     */
    public TicketType toEntity() {
        final var type = new TicketType();
        type.id = this.id;
        type.name = this.name;
        type.description = this.description;
        type.icon = this.icon;
        type.color = this.color;
        return type;
    }
}
