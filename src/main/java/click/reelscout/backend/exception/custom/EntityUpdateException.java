package click.reelscout.backend.exception.custom;

public class EntityUpdateException extends RuntimeException {
    public <T> EntityUpdateException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " update failed");
    }

    public EntityUpdateException(String message) {
        super(message);
    }
}
