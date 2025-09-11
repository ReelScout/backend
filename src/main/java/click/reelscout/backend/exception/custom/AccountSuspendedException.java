package click.reelscout.backend.exception.custom;

/**
 * Custom exception thrown when an account is suspended.
 */
public class AccountSuspendedException extends RuntimeException {
    /**
     * Constructs a new AccountSuspendedException with the specified detail message.
     *
     * @param message the detail message
     */
    public AccountSuspendedException(String message) {
        super(message);
    }
}

