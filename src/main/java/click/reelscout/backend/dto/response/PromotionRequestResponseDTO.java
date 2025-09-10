package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.PromotionRequestStatus;
import click.reelscout.backend.model.jpa.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PromotionRequestResponseDTO {
    private Long id;
    private Long requesterId;
    private String requesterUsername;
    private PromotionRequestStatus status;
    private Role requestedRole;
    private String message;
    private String decisionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

