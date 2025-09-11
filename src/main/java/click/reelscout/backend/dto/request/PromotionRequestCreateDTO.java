package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a promotion request.
 */
@Data
public class PromotionRequestCreateDTO {
    @Size(max = 1000)
    private String message;
}

