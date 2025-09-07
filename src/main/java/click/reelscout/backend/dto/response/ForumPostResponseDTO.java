package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ForumPostResponseDTO {
    private Long id;
    private Long threadId;
    private String authorUsername;
    private String body;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

