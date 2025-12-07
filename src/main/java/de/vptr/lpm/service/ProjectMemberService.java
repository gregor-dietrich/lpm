package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.ProjectMemberDto;
import de.vptr.lpm.entity.ProjectMember;
import de.vptr.lpm.repository.ProjectMemberRepository;
import de.vptr.lpm.repository.ProjectRepository;
import de.vptr.lpm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing project members.
 */
@ApplicationScoped
public class ProjectMemberService {

    @Inject
    ProjectMemberRepository repository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    UserRepository userRepository;

    /**
     * Add a member to a project.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     * @param role      the member's role in the project
     * @return the created project member DTO
     */
    @Transactional
    public ProjectMemberDto addMember(
            final Long projectId,
            final Long userId,
            final String role) {
        // Check if member already exists
        if (this.repository.findByProjectAndUser(projectId, userId) != null) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        final var project = this.projectRepository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        final var user = this.userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        final var member = new ProjectMember();
        member.project = project;
        member.user = user;
        member.role = role;

        this.repository.persist(member);
        return ProjectMemberDto.fromEntity(member);
    }

    /**
     * Remove a member from a project.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     */
    @Transactional
    public void removeMember(final Long projectId, final Long userId) {
        final var member = this.repository.findByProjectAndUser(projectId, userId);
        if (member != null) {
            this.repository.delete(member);
        }
    }

    /**
     * Get all members of a project.
     *
     * @param projectId the project ID
     * @return list of project members
     */
    public List<ProjectMemberDto> findByProject(final Long projectId) {
        return this.repository.findByProject(projectId).stream()
                .map(ProjectMemberDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Find a project member by project and user.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     * @return an Optional containing the project member if found
     */
    public Optional<ProjectMemberDto> findByProjectAndUser(final Long projectId, final Long userId) {
        final var member = this.repository.findByProjectAndUser(projectId, userId);
        return Optional.ofNullable(member)
                .map(ProjectMemberDto::fromEntity);
    }

    /**
     * Update a member's role.
     *
     * @param projectId the project ID
     * @param userId    the user ID
     * @param newRole   the new role
     * @return the updated project member DTO
     */
    @Transactional
    public ProjectMemberDto updateMemberRole(
            final Long projectId,
            final Long userId,
            final String newRole) {
        final var member = this.repository.findByProjectAndUser(projectId, userId);
        if (member == null) {
            throw new IllegalArgumentException("Project member not found");
        }

        member.role = newRole;
        this.repository.persist(member);
        return ProjectMemberDto.fromEntity(member);
    }

    /**
     * Count members of a project.
     *
     * @param projectId the project ID
     * @return total count of project members
     */
    public long countMembers(final Long projectId) {
        return this.repository.count("project.id = ?1", projectId);
    }
}
