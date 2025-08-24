package click.reelscout.backend.exception.custom;

public class EntityCreateException extends RuntimeException {
    public <T> EntityCreateException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " registration failed");
    }

    public EntityCreateException(String message) {
        super(message);
    }
}
