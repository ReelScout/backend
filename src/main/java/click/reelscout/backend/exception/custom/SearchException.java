package click.reelscout.backend.exception.custom;

public class SearchException extends RuntimeException {
    public SearchException() {
        super("An error occurred while performing the search operation.");
    }
}
