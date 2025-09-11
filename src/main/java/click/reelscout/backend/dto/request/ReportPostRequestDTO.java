package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for reporting a post.
 */
@Data
public class ReportPostRequestDTO {
    @NotBlank(message = "Reason is mandatory")
    private String reason;
}

