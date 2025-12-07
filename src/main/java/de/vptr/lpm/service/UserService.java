package de.vptr.lpm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vptr.lpm.dto.UserDto;
import de.vptr.lpm.entity.User;
import de.vptr.lpm.enums.UserStatus;
import de.vptr.lpm.repository.RoleRepository;
import de.vptr.lpm.repository.UserRepository;
import de.vptr.lpm.security.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for managing users and authentication.
 */
@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return Optional containing the UserDTO if found
     */
    public Optional<UserDto> findById(final Long id) {
        return this.userRepository.findByIdOptional(id)
                .map(UserDto::fromEntity);
    }

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return Optional containing the UserDTO if found
     */
    public Optional<UserDto> findByUsername(final String username) {
        return this.userRepository.findByUsername(username)
                .map(UserDto::fromEntity);
    }

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return Optional containing the UserDTO if found
     */
    public Optional<UserDto> findByEmail(final String email) {
        return this.userRepository.findByEmail(email)
                .map(UserDto::fromEntity);
    }

    /**
     * Lists all users.
     *
     * @return list of UserDTOs
     */
    public List<UserDto> listAll() {
        return this.userRepository.listAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user with the given credentials.
     *
     * @param username    the username
     * @param email       the email
     * @param password    the plain-text password (will be hashed)
     * @param displayName the display name
     * @return the created UserDTO
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public UserDto createUser(
            final String username,
            final String email,
            final String password,
            final String displayName) {
        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (this.userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        final var user = new User();
        user.username = username;
        user.email = email;
        user.passwordHash = PasswordUtil.hashPassword(password);
        user.displayName = displayName;
        user.status = UserStatus.ACTIVE;

        this.userRepository.persist(user);
        return UserDto.fromEntity(user);
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the username
     * @param password the plain-text password to verify
     * @return Optional containing the UserDTO if authentication succeeds
     */
    public Optional<UserDto> authenticate(final String username, final String password) {
        return this.userRepository.findByUsername(username)
                .filter(user -> user.status == UserStatus.ACTIVE)
                .filter(user -> PasswordUtil.verifyPassword(password, user.passwordHash))
                .map(UserDto::fromEntity);
    }

    /**
     * Updates a user's basic information (name, email, status).
     *
     * @param id          the user ID
     * @param username    the username (not updated, used for reference)
     * @param email       the new email
     * @param displayName the new display name
     * @return the updated UserDTO
     */
    public UserDto updateUser(final Long id, final String username, final String email, final String displayName) {
        return this.userRepository.findByIdOptional(id)
                .map(user -> {
                    user.email = email;
                    user.displayName = displayName;
                    this.userRepository.persist(user);
                    return UserDto.fromEntity(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    /**
     * Updates a user's profile information via DTO.
     *
     * @param id  the user ID
     * @param dto the updated UserDTO (password hash is ignored)
     * @return the updated UserDTO
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public UserDto updateUser(final Long id, final UserDto dto) {
        return this.userRepository.findByIdOptional(id)
                .map(user -> {
                    user.email = dto.email();
                    user.displayName = dto.displayName();
                    user.status = dto.status();
                    this.userRepository.persist(user);
                    return UserDto.fromEntity(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    /**
     * Changes a user's password.
     *
     * @param id          the user ID
     * @param newPassword the new plain-text password
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void changePassword(final Long id, final String newPassword) {
        this.userRepository.findByIdOptional(id)
                .ifPresentOrElse(
                        user -> {
                            user.passwordHash = PasswordUtil.hashPassword(newPassword);
                            this.userRepository.persist(user);
                        },
                        () -> {
                            throw new IllegalArgumentException("User not found with ID: " + id);
                        });
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void deleteUser(final Long id) {
        if (this.userRepository.deleteById(id)) {
            return;
        }
        throw new IllegalArgumentException("User not found with ID: " + id);
    }

    /**
     * Counts the total number of users.
     *
     * @return the user count
     */
    public long countUsers() {
        return this.userRepository.count();
    }
}
