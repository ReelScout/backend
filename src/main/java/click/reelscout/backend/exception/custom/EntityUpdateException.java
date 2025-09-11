package click.reelscout.backend.exception.custom;

/**
 * Exception thrown when an entity update operation fails.
 */
public class EntityUpdateException extends RuntimeException {
    public <T> EntityUpdateException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " update failed");
    }

    /**
     * Constructs a new EntityUpdateException with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityUpdateException(String message) {
        super(message);
    }
}
