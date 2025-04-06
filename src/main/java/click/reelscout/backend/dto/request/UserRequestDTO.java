package click.reelscout.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Valid
public class UserRequestDTO {
        @NotBlank(message = "Firstname is mandatory")
        private String firstName;

        @NotBlank(message = "Lastname is mandatory")
        private String lastName;

        @NotNull(message = "Birthdate is mandatory")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate birthDate;

        @NotBlank(message = "Username is mandatory")
        private String username;

        @NotBlank(message = "Email is mandatory")
        @Email(message = "The email address is not valid")
        private String email;

        @NotBlank(message = "Password is mandatory")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain one uppercase letter, one special character, and one number"
        )
        private String password;
}
