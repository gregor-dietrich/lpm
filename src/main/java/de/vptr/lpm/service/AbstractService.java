package de.vptr.lpm.service;

import java.util.Optional;

import de.vptr.lpm.entity.AbstractEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.transaction.Transactional;

/**
 * Base service class providing common CRUD operations for entities extending
 * {@link AbstractEntity}.
 *
 * @param <T> the entity type
 * @param <R> the repository type
 */
public abstract class AbstractService<T extends AbstractEntity, R extends PanacheRepository<T>> {

    /**
     * The repository instance for database operations.
     */
    protected final R repository;

    /**
     * Constructor for AbstractService.
     *
     * @param repository the repository instance
     */
    protected AbstractService(final R repository) {
        this.repository = repository;
    }

    /**
     * Creates a new entity.
     *
     * @param entity the entity to create
     * @return the created entity with generated ID
     */
    @Transactional
    public T create(final T entity) {
        this.repository.persist(entity);
        return entity;
    }

    /**
     * Finds an entity by ID.
     *
     * @param id the entity ID
     * @return an Optional containing the entity, or empty if not found
     */
    public Optional<T> findById(final Long id) {
        return this.repository.findByIdOptional(id);
    }

    /**
     * Updates an entity.
     *
     * @param entity the entity with updated values
     * @return the updated entity
     */
    @Transactional
    public T update(final T entity) {
        return this.repository.getEntityManager().merge(entity);
    }

    /**
     * Deletes an entity by ID.
     *
     * @param id the entity ID
     * @return true if the entity was deleted, false if not found
     */
    @Transactional
    public boolean deleteById(final Long id) {
        return this.repository.deleteById(id);
    }

    /**
     * Counts all entities of this type.
     *
     * @return the number of entities
     */
    public long count() {
        return this.repository.count();
    }
}
