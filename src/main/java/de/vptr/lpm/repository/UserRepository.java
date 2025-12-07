package de.vptr.lpm.repository;

import java.util.Optional;

import de.vptr.lpm.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for User entity providing database access operations.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsername(final String username) {
        return this.find("username", username).firstResultOptional();
    }

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(final String email) {
        return this.find("email", email).firstResultOptional();
    }
}
