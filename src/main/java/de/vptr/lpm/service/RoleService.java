package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.RoleDto;
import de.vptr.lpm.entity.Role;
import de.vptr.lpm.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing roles.
 */
@ApplicationScoped
public class RoleService {

    @Inject
    RoleRepository roleRepository;

    /**
     * Finds a role by ID.
     *
     * @param id the role ID
     * @return Optional containing the RoleDTO if found
     */
    public Optional<RoleDto> findById(final Long id) {
        return this.roleRepository.findByIdOptional(id)
                .map(RoleDto::fromEntity);
    }

    /**
     * Finds a role by name.
     *
     * @param name the role name
     * @return Optional containing the RoleDTO if found
     */
    public Optional<RoleDto> findByName(final String name) {
        return this.roleRepository.findByName(name)
                .map(RoleDto::fromEntity);
    }

    /**
     * Lists all roles ordered by sort order.
     *
     * @return list of RoleDTOs sorted by order
     */
    public List<RoleDto> listAll() {
        return this.roleRepository.find("SELECT r FROM Role r ORDER BY r.order").stream()
                .map(RoleDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new role.
     *
     * @param name        the role name (must be unique)
     * @param description the role description
     * @param color       the color code
     * @param icon        the icon name
     * @param order       the sort order
     * @return the created RoleDTO
     * @throws IllegalArgumentException if role name already exists
     */
    @Transactional
    public RoleDto createRole(
            final String name,
            final String description,
            final String color,
            final String icon,
            final Integer order) {
        if (this.roleRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        final var role = new Role();
        role.name = name;
        role.description = description;
        role.color = color;
        role.icon = icon;
        role.order = order;

        this.roleRepository.persist(role);
        return RoleDto.fromEntity(role);
    }

    /**
     * Updates an existing role.
     *
     * @param id  the role ID
     * @param dto the updated RoleDTO
     * @return the updated RoleDTO
     * @throws IllegalArgumentException if role not found
     */
    @Transactional
    public RoleDto updateRole(final Long id, final RoleDto dto) {
        return this.roleRepository.findByIdOptional(id)
                .map(role -> {
                    role.name = dto.name();
                    role.description = dto.description();
                    role.color = dto.color();
                    role.icon = dto.icon();
                    role.order = dto.order();
                    this.roleRepository.persist(role);
                    return RoleDto.fromEntity(role);
                })
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id));
    }

    /**
     * Deletes a role by ID.
     *
     * @param id the role ID
     * @throws IllegalArgumentException if role not found
     */
    @Transactional
    public void deleteRole(final Long id) {
        if (this.roleRepository.deleteById(id)) {
            return;
        }
        throw new IllegalArgumentException("Role not found with ID: " + id);
    }

    /**
     * Counts the total number of roles.
     *
     * @return the role count
     */
    public long countRoles() {
        return this.roleRepository.count();
    }
}
