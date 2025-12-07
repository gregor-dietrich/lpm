package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.Project;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for Project entity with custom query methods.
 */
@ApplicationScoped
public class ProjectRepository implements PanacheRepository<Project> {

    /**
     * Find all projects owned by a user.
     *
     * @param userId the user ID
     * @return list of projects owned by the user
     */
    public List<Project> findByOwner(final Long userId) {
        return this.find("owner.id = ?1", userId).list();
    }

    /**
     * Find a project by its key.
     *
     * @param projectKey the project key
     * @return the project if found, or null
     */
    public Project findByKey(final String projectKey) {
        return this.find("projectKey", projectKey).firstResult();
    }
}
