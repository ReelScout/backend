package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromotionRequestCreateDTO {
    @Size(max = 1000)
    private String message;
}

