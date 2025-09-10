package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.*;

import java.time.LocalDateTime;

public interface PromotionRequestBuilder extends EntityBuilder<PromotionRequest, PromotionRequestBuilder> {
    PromotionRequestBuilder requester(Member requester);
    PromotionRequestBuilder status(PromotionRequestStatus status);
    PromotionRequestBuilder requestedRole(Role requestedRole);
    PromotionRequestBuilder message(String message);
    PromotionRequestBuilder decisionReason(String decisionReason);
    PromotionRequestBuilder processedBy(User processedBy);
    PromotionRequestBuilder createdAt(LocalDateTime createdAt);
    PromotionRequestBuilder updatedAt(LocalDateTime updatedAt);
}

