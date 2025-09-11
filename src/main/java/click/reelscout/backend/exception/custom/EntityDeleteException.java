package click.reelscout.backend.exception.custom;

/**
 * Exception thrown when an entity deletion operation fails.
 */
public class EntityDeleteException extends RuntimeException {
    /** Constructs a new EntityDeleteException with a message indicating the entity class that failed to delete.
     *
     * @param entityClass the class of the entity that failed to delete
     */
    public <T> EntityDeleteException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " delete failed");
    }

    /** Constructs a new EntityDeleteException with a custom message.
     *
     * @param message the detail message
     */
    public EntityDeleteException(String message) {
        super(message);
    }
}
