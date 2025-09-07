package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequestDTO {
    @NotBlank(message = "Body is mandatory")
    private String body;

    private Long parentId;
}

