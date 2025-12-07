package de.vptr.lpm.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents an activity log entry for tracking changes to entities.
 * Activity logs create an audit trail of all significant changes in the system.
 */
@Entity
@Table(name = "activity_log")
public class ActivityLog extends AbstractEntity {

    /**
     * The type of entity that was modified (e.g., "Ticket", "Project").
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    public String entityType;

    /**
     * The ID of the entity that was modified.
     */
    @NotNull
    @Column(nullable = false)
    public Long entityId;

    /**
     * The action that was performed (e.g., "CREATE", "UPDATE", "DELETE").
     */
    @NotNull
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    public String action;

    /**
     * The user who performed the action.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * Additional details about the action (e.g., what was changed).
     */
    public String details;

    /**
     * Find all activity logs for an entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of activity logs for the entity
     */
    public static List<ActivityLog> findByEntity(final String entityType, final Long entityId) {
        return find("entityType = ?1 and entityId = ?2", entityType, entityId)
                .list();
    }

    /**
     * Find all activity logs by a user.
     *
     * @param userId the user ID
     * @return list of activity logs by the user
     */
    public static List<ActivityLog> findByUser(final Long userId) {
        return find("user.id", userId).list();
    }
}
