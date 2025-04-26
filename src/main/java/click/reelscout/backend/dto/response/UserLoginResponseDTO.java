package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserLoginResponseDTO extends UserResponseDTO {
    private String token;
}
