package click.reelscout.backend.exception.custom;

/**
 * Custom exception for data validation errors.
 */
public class DataValidationException extends RuntimeException {
    /**
     * Constructs a new DataValidationException with the specified detail message.
     *
     * @param message the detail message.
     */
    public DataValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataValidationException with a default detail message.
     */
    public DataValidationException() {
        super("Data validation failed.");
    }
}
