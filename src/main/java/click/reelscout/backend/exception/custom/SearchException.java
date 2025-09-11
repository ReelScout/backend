package click.reelscout.backend.exception.custom;

/**
 * Custom exception class for handling search-related errors.
 */
public class SearchException extends RuntimeException {
    /**
     * Constructs a new SearchException with a default error message.
     */
    public SearchException() {
        super("An error occurred while performing the search operation.");
    }
}
