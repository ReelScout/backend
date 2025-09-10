package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerificationDecisionRequestDTO {
    @Size(max = 1000)
    private String reason;
}

