package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO {
    @NotBlank(message = "Username or e-mail is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
