package click.reelscout.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for handling requests to create a forum post.
 * Contains the body of the post and an optional parent post ID for replies.
 */
@Data
public class CreatePostRequestDTO {
    @NotBlank(message = "Body is mandatory")
    private String body;

    private Long parentId;
}
