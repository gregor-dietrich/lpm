package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.ProjectStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for ProjectStatus entity with custom query methods.
 */
@ApplicationScoped
public class ProjectStatusRepository implements PanacheRepository<ProjectStatus> {

    /**
     * Find all project statuses ordered by order field.
     *
     * @return list of all project statuses
     */
    public List<ProjectStatus> findAllOrdered() {
        return this.find("ORDER BY order").list();
    }
}
