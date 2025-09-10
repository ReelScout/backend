package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromotionDecisionRequestDTO {
    @Size(max = 1000)
    private String reason;
}

