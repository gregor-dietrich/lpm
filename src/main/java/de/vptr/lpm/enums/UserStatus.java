package de.vptr.lpm.enums;

/**
 * Enumeration of possible user account statuses. These are system-level states
 * that are not configurable.
 */
public enum UserStatus {
    /**
     * User account is active and can log in.
     */
    ACTIVE,

    /**
     * User account is inactive and cannot log in.
     */
    INACTIVE,

    /**
     * User account is locked due to security reasons (e.g., too many failed login
     * attempts).
     */
    LOCKED
}
