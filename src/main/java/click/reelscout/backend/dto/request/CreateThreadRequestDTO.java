package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new thread.
 */
@Data
public class CreateThreadRequestDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Body is mandatory")
    private String body;
}

