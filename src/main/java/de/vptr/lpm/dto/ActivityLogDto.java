package de.vptr.lpm.dto;

import java.time.LocalDateTime;

import de.vptr.lpm.entity.ActivityLog;

/**
 * Data transfer object for ActivityLog entity.
 *
 * @param id         unique identifier
 * @param entityType type of entity modified
 * @param entityId   ID of the entity modified
 * @param action     action performed
 * @param userId     ID of the user who performed the action
 * @param userName   display name of the user
 * @param details    additional details about the action
 * @param createdAt  timestamp of the activity
 */
public record ActivityLogDto(
        Long id,
        String entityType,
        Long entityId,
        String action,
        Long userId,
        String userName,
        String details,
        LocalDateTime createdAt) {

    /**
     * Creates an ActivityLogDTO from an ActivityLog entity.
     *
     * @param log the activity log entity
     * @return an ActivityLogDTO with the log's data
     */
    public static ActivityLogDto fromEntity(final ActivityLog log) {
        return new ActivityLogDto(
                log.id,
                log.entityType,
                log.entityId,
                log.action,
                log.user != null ? log.user.id : null,
                log.user != null ? log.user.displayName : null,
                log.details,
                log.getCreatedAt());
    }

    /**
     * Converts an ActivityLogDTO to an ActivityLog entity. Note: does not set user.
     *
     * @return a new ActivityLog entity with data from this DTO
     */
    public ActivityLog toEntity() {
        final var log = new ActivityLog();
        log.id = this.id;
        log.entityType = this.entityType;
        log.entityId = this.entityId;
        log.action = this.action;
        log.details = this.details;
        return log;
    }
}
