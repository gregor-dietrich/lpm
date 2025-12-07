package de.vptr.lpm.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.vptr.lpm.entity.User;
import de.vptr.lpm.enums.UserStatus;

/**
 * Data transfer object for User entity. Does not include password hash.
 *
 * @param id          unique identifier
 * @param username    unique username
 * @param email       email address
 * @param displayName display name shown in UI
 * @param status      user account status
 * @param roles       set of role DTOs
 * @param createdAt   creation timestamp
 * @param updatedAt   last update timestamp
 */
public record UserDto(
                Long id,
                String username,
                String email,
                String displayName,
                UserStatus status,
                Set<RoleDto> roles,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

        /**
         * Creates a UserDTO from a User entity.
         *
         * @param user the user entity
         * @return a UserDTO with the user's data (excluding password hash)
         */
        public static UserDto fromEntity(final User user) {
                return new UserDto(
                                user.id,
                                user.username,
                                user.email,
                                user.displayName,
                                user.status,
                                Collections.unmodifiableSet(user.roles.stream()
                                                .map(RoleDto::fromEntity)
                                                .collect(Collectors.toSet())),
                                user.getCreatedAt(),
                                user.getUpdatedAt());
        }

        /**
         * Converts a UserDTO to a User entity. Note: does not set password hash.
         *
         * @return a new User entity with data from this DTO
         */
        public User toEntity() {
                final var user = new User();
                user.id = this.id;
                user.username = this.username;
                user.email = this.email;
                user.displayName = this.displayName;
                user.status = this.status;
                user.roles = this.roles.stream()
                                .map(RoleDto::toEntity)
                                .collect(Collectors.toSet());
                return user;
        }
}
