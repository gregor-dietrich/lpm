package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.ActivityLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for ActivityLog entity with custom query methods.
 */
@ApplicationScoped
public class ActivityLogRepository implements PanacheRepository<ActivityLog> {

    /**
     * Find all activity logs for an entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of activity logs for the entity
     */
    public List<ActivityLog> findByEntity(final String entityType, final Long entityId) {
        return this.find("entityType = ?1 and entityId = ?2 order by createdAt desc", entityType, entityId).list();
    }

    /**
     * Find all activity logs by a user.
     *
     * @param userId the user ID
     * @return list of activity logs by the user
     */
    public List<ActivityLog> findByUser(final Long userId) {
        return this.find("user.id = ?1 order by createdAt desc", userId).list();
    }
}
