package click.reelscout.backend.exception.custom;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }
}
