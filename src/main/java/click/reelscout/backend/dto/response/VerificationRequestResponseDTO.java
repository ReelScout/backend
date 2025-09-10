package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.VerificationRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VerificationRequestResponseDTO {
    private Long id;
    private Long requesterId;
    private String requesterUsername;
    private VerificationRequestStatus status;
    private String message;
    private String decisionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

