package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.ActivityLogDto;
import de.vptr.lpm.entity.ActivityLog;
import de.vptr.lpm.repository.ActivityLogRepository;
import de.vptr.lpm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing activity logs and audit trails.
 */
@ApplicationScoped
public class ActivityLogService {

    @Inject
    ActivityLogRepository repository;

    @Inject
    UserRepository userRepository;

    /**
     * Log an activity/action.
     *
     * @param entityType the type of entity modified
     * @param entityId   the ID of the entity modified
     * @param action     the action performed
     * @param userId     the ID of the user who performed the action
     * @param details    additional details about the action
     * @return the created activity log DTO
     */
    @Transactional
    public ActivityLogDto logActivity(
            final String entityType,
            final Long entityId,
            final String action,
            final Long userId,
            final String details) {
        final var log = new ActivityLog();
        log.entityType = entityType;
        log.entityId = entityId;
        log.action = action;
        log.details = details;

        if (userId != null) {
            final var user = this.userRepository.findById(userId);
            if (user != null) {
                log.user = user;
            }
        }

        this.repository.persist(log);
        return ActivityLogDto.fromEntity(log);
    }

    /**
     * Find an activity log by ID.
     *
     * @param id the activity log ID
     * @return an Optional containing the log if found
     */
    public Optional<ActivityLogDto> findById(final Long id) {
        return this.repository.findByIdOptional(id)
                .map(ActivityLogDto::fromEntity);
    }

    /**
     * Get all activity logs for an entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of activity logs
     */
    public List<ActivityLogDto> findByEntity(final String entityType, final Long entityId) {
        return this.repository.findByEntity(entityType, entityId).stream()
                .map(ActivityLogDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all activity logs by a user.
     *
     * @param userId the user ID
     * @return list of activity logs
     */
    public List<ActivityLogDto> findByUser(final Long userId) {
        return this.repository.findByUser(userId).stream()
                .map(ActivityLogDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Count activity logs for an entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return total count of logs
     */
    public long countByEntity(final String entityType, final Long entityId) {
        return this.repository.count("entityType = ?1 and entityId = ?2", entityType, entityId);
    }
}
