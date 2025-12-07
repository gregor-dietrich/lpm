package de.vptr.lpm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.entity.User;
import de.vptr.lpm.security.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for handling user authentication.
 * Bridges Vaadin session-based auth with application logic.
 */
@ApplicationScoped
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);
    private static final String SESSION_USER_ATTR = "user";

    /**
     * Authenticate a user by username and password.
     *
     * @param username the username
     * @param password the plaintext password
     * @return Optional containing UserDto if authentication succeeds
     */
    public Optional<UserDto> authenticate(final String username, final String password) {
        return User.find("username", username)
                .singleResultOptional()
                .filter(user -> this.validatePassword((User) user, password))
                .map(user -> UserDto.fromEntity((User) user));
    }

    /**
     * Get the currently authenticated user from the session.
     *
     * @param request the HTTP request
     * @return Optional containing current UserDto
     */
    public Optional<UserDto> getCurrentUser(final HttpServletRequest request) {
        final var session = request.getSession(false);
        if (session != null) {
            final var user = (UserDto) session.getAttribute(SESSION_USER_ATTR);
            if (user != null) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the currently authenticated user (without request context).
     * This is a fallback that tries to access the session from Vaadin context.
     *
     * @return Optional containing current UserDto
     */
    public Optional<UserDto> getCurrentUser() {
        try {
            // Try to get from Vaadin session if available
            final var vaadinSession = com.vaadin.flow.server.VaadinSession.getCurrent();
            if (vaadinSession != null) {
                final var user = (UserDto) vaadinSession.getAttribute(SESSION_USER_ATTR);
                if (user != null) {
                    return Optional.of(user);
                }
            }
        } catch (final Exception e) {
            LOG.debug("Could not access Vaadin session", e);
        }
        return Optional.empty();
    }

    /**
     * Validate a user's password against the stored hash.
     *
     * @param user          the user entity
     * @param plainPassword the plaintext password to validate
     * @return true if password is valid
     */
    private boolean validatePassword(final User user, final String plainPassword) {
        try {
            return PasswordUtil.verifyPassword(plainPassword, user.passwordHash);
        } catch (final Exception e) {
            LOG.warn("Password validation failed for user: {}", user.username, e);
            return false;
        }
    }
}
