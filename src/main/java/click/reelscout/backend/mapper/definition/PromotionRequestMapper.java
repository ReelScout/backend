package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.PromotionRequestBuilder;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.*;

/**
 * Mapper interface for converting between {@link PromotionRequest} entities, DTOs, and builders.
 */
public interface PromotionRequestMapper {
    /**
     * Converts a {@link PromotionRequest} entity to its corresponding DTO.
     *
     * @param request the promotion request entity to convert
     * @return the corresponding {@link PromotionRequestResponseDTO}
     */
    PromotionRequestResponseDTO toDto(PromotionRequest request);

    /**
     * Converts a {@link PromotionRequest} entity to its builder representation.
     *
     * @param request the promotion request entity to convert
     * @return the corresponding {@link PromotionRequestBuilder}
     */
    PromotionRequestBuilder toBuilder(PromotionRequest request);

    /**
     * Creates a {@link PromotionRequest} entity from the given requester, message, status, and role.
     *
     * @param requester the member requesting the promotion
     * @param message the message accompanying the promotion request
     * @param status the status of the promotion request
     * @param requestedRole the role being requested
     * @return the created {@link PromotionRequest} entity
     */
    PromotionRequest toEntity(Member requester, String message, PromotionRequestStatus status, Role requestedRole);
}
