package click.reelscout.backend.exception.custom;

/**
 * Exception thrown when invalid credentials are provided.
 */
public class InvalidCredentialsException extends RuntimeException {
    /**
     * Constructs a new InvalidCredentialsException with a default message.
     */
    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }
}
