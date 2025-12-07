package de.vptr.lpm.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.vptr.lpm.enums.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a user in the system. Stores authentication credentials and user
 * profile information.
 */
@Entity
@Table(name = "\"user\"")
public class User extends AbstractEntity {

    /**
     * Unique username for login.
     */
    @NotNull
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    public String username;

    /**
     * Email address for communication.
     */
    @NotNull
    @Email
    @Column(unique = true, nullable = false)
    public String email;

    /**
     * BCrypt hashed password. Never expose to frontend.
     */
    @NotNull
    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    /**
     * Display name shown in the UI.
     */
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "display_name", nullable = false)
    public String displayName;

    /**
     * Current user status (ACTIVE, INACTIVE, LOCKED).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserStatus status;

    /**
     * Roles assigned to this user.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> roles = new HashSet<>();

    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    public static Optional<User> findByUsername(final String username) {
        return find("username", username).firstResultOptional();
    }

    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    public static Optional<User> findByEmail(final String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Find all active users.
     *
     * @return a list of active users
     */
    public static PanacheQuery<User> findActive() {
        return find("status", UserStatus.ACTIVE);
    }
}
