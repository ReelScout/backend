package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.*;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link PromotionRequest} instances.
 */
public interface PromotionRequestBuilder extends EntityBuilder<PromotionRequest, PromotionRequestBuilder> {
    /** Set the requester. */
    PromotionRequestBuilder requester(Member requester);
    /** Set request status. */
    PromotionRequestBuilder status(PromotionRequestStatus status);
    /** Set requested role. */
    PromotionRequestBuilder requestedRole(Role requestedRole);
    /** Set optional message. */
    PromotionRequestBuilder message(String message);
    /** Set decision reason. */
    PromotionRequestBuilder decisionReason(String decisionReason);
    /** Set user who processed the request. */
    PromotionRequestBuilder processedBy(User processedBy);
    /** Set creation timestamp. */
    PromotionRequestBuilder createdAt(LocalDateTime createdAt);
    /** Set update timestamp. */
    PromotionRequestBuilder updatedAt(LocalDateTime updatedAt);
}
