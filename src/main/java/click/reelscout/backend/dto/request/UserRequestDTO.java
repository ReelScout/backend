package click.reelscout.backend.dto.request;

public record UserRequestDTO (String username, String email, String password, String passwordConfirmation) {
}
