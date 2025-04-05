package click.reelscout.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Valid
public record UserRequestDTO (
        @NotBlank(message = "Username is mandatory")
        String username,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "The email address is not valid")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain one uppercase letter, one special character, and one number"
        )
        String password
) {
}
