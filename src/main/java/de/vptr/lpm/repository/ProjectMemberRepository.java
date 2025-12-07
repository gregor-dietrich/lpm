package de.vptr.lpm.repository;

import java.util.List;

import de.vptr.lpm.entity.ProjectMember;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for ProjectMember entity with custom query methods.
 */
@ApplicationScoped
public class ProjectMemberRepository implements PanacheRepository<ProjectMember> {

    /**
     * Find all members of a project.
     *
     * @param projectId the project ID
     * @return list of project members
     */
    public List<ProjectMember> findByProject(final Long projectId) {
        return this.find("project.id = ?1", projectId).list();
    }

    /**
     * Find a project member by project and user.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     * @return the project member if found, or null
     */
    public ProjectMember findByProjectAndUser(final Long projectId, final Long userId) {
        return this.find("project.id = ?1 and user.id = ?2", projectId, userId).firstResult();
    }
}
