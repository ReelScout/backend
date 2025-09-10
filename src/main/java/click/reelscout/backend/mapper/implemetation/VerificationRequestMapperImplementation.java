package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.VerificationRequestBuilder;
import click.reelscout.backend.dto.response.VerificationRequestResponseDTO;
import click.reelscout.backend.mapper.definition.VerificationRequestMapper;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.VerificationRequest;
import click.reelscout.backend.model.jpa.VerificationRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationRequestMapperImplementation implements VerificationRequestMapper {
    private final VerificationRequestBuilder builder;

    @Override
    public VerificationRequestResponseDTO toDto(VerificationRequest request) {
        return new VerificationRequestResponseDTO(
                request.getId(),
                request.getRequester().getId(),
                request.getRequester().getUsername(),
                request.getStatus(),
                request.getMessage(),
                request.getDecisionReason(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    @Override
    public VerificationRequestBuilder toBuilder(VerificationRequest request) {
        return builder
                .id(request.getId())
                .requester(request.getRequester())
                .status(request.getStatus())
                .message(request.getMessage())
                .decisionReason(request.getDecisionReason())
                .processedBy(request.getProcessedBy())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt());
    }

    @Override
    public VerificationRequest toEntity(Member requester, String message, VerificationRequestStatus status) {
        return builder
                .id(null)
                .requester(requester)
                .status(status)
                .message(message)
                .decisionReason(null)
                .processedBy(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}

