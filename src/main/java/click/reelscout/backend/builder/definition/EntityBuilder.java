package click.reelscout.backend.builder.definition;

/**
 * Generic builder contract for entities used across the application.
 *
 * @param <E> entity type produced by the builder
 * @param <B> concrete builder type for fluent APIs
 */
public interface EntityBuilder<E, B extends EntityBuilder<E, B>> {
    /** Set the unique identifier for the entity. */
    B id(Long id);
    /** Build and return the entity instance. */
    E build();
}
