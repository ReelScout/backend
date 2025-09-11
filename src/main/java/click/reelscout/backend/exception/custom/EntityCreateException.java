package click.reelscout.backend.exception.custom;

/**
 * Exception thrown when an entity creation fails.
 */
public class EntityCreateException extends RuntimeException {
    /** Constructs a new EntityCreateException with a message indicating the entity class that failed to be created.
     *
     * @param entityClass the class of the entity that failed to be created
     */
    public <T> EntityCreateException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " registration failed");
    }

    /** Constructs a new EntityCreateException with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityCreateException(String message) {
        super(message);
    }
}
