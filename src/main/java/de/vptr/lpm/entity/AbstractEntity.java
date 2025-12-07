package de.vptr.lpm.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

/**
 * Base class for all entities in the system. Provides common fields: id,
 * createdAt, and updatedAt with automatic audit trail support.
 */
@MappedSuperclass
public abstract class AbstractEntity extends PanacheEntityBase {

    /**
     * Unique identifier for the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * Timestamp when the entity was first created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Gets the creation timestamp.
     *
     * @return the createdAt timestamp
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the updatedAt timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Automatically set createdAt and updatedAt before persisting a new entity.
     */
    @PrePersist
    protected void onCreate() {
        final var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Automatically update the updatedAt timestamp before updating an entity.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "%s(id=%s)".formatted(this.getClass().getSimpleName(), this.id);
    }
}
