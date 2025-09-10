package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.PromotionRequestBuilder;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.*;

public interface PromotionRequestMapper {
    PromotionRequestResponseDTO toDto(PromotionRequest request);

    PromotionRequestBuilder toBuilder(PromotionRequest request);

    PromotionRequest toEntity(Member requester, String message, PromotionRequestStatus status, Role requestedRole);
}

