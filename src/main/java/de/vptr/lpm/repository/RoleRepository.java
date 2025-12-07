package de.vptr.lpm.repository;

import java.util.Optional;

import de.vptr.lpm.entity.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for Role entity providing database access operations.
 */
@ApplicationScoped
public class RoleRepository implements PanacheRepository<Role> {

    /**
     * Finds a role by name.
     *
     * @param name the role name to search for
     * @return Optional containing the role if found
     */
    public Optional<Role> findByName(final String name) {
        return this.find("name", name).firstResultOptional();
    }
}
