package click.reelscout.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling requests to ban a user.
 * Contains the reason for banning the user.
 */
@NoArgsConstructor
@Data
public class BanUserRequestDTO {
    private String reason;
}
