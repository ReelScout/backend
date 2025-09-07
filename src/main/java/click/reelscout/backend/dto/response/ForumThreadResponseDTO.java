package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ForumThreadResponseDTO {
    private Long id;
    private Long contentId;
    private String title;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long postCount;
}

