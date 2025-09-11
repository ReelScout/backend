package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.PromotionRequestBuilder;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.mapper.definition.PromotionRequestMapper;
import click.reelscout.backend.model.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionRequestMapperImplementation implements PromotionRequestMapper {
    private final PromotionRequestBuilder builder;

    /** {@inheritDoc} */
    @Override
    public PromotionRequestResponseDTO toDto(PromotionRequest request) {
        return new PromotionRequestResponseDTO(
                request.getId(),
                request.getRequester().getId(),
                request.getRequester().getUsername(),
                request.getStatus(),
                request.getRequestedRole(),
                request.getMessage(),
                request.getDecisionReason(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder toBuilder(PromotionRequest request) {
        return builder
                .id(request.getId())
                .requester(request.getRequester())
                .status(request.getStatus())
                .requestedRole(request.getRequestedRole())
                .message(request.getMessage())
                .decisionReason(request.getDecisionReason())
                .processedBy(request.getProcessedBy())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt());
    }

    /** {@inheritDoc} */
    @Override
    public PromotionRequest toEntity(Member requester, String message, PromotionRequestStatus status, Role requestedRole) {
        return builder
                .id(null)
                .requester(requester)
                .status(status)
                .requestedRole(requestedRole)
                .message(message)
                .decisionReason(null)
                .processedBy(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}

