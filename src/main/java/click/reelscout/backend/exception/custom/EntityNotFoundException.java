package click.reelscout.backend.exception.custom;

/**
 * Custom exception thrown when an entity is not found in the database.
 */
public class EntityNotFoundException extends RuntimeException{
    /**
     * Constructs a new EntityNotFoundException with a message indicating the entity class that was not found.
     *
     * @param entityClass the class of the entity that was not found
     */
    public <T> EntityNotFoundException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " not found");
    }
}
