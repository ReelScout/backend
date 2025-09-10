package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportPostRequestDTO {
    @NotBlank(message = "Reason is mandatory")
    private String reason;
}

