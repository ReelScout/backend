package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for making a promotion decision (approve or reject) on a content creator application.
 */
@Data
public class PromotionDecisionRequestDTO {
    @Size(max = 1000)
    private String reason;
}

