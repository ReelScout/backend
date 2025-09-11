package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * CustomResponseDTO is a simple DTO for sending custom messages in API responses.
 */
@Getter
@AllArgsConstructor
public class CustomResponseDTO {
    private String message;
}
