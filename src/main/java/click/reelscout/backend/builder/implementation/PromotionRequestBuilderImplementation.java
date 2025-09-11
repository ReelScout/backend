package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.PromotionRequestBuilder;
import click.reelscout.backend.model.jpa.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class PromotionRequestBuilderImplementation implements PromotionRequestBuilder {
    private Long id;
    private Member requester;
    private PromotionRequestStatus status;
    private Role requestedRole;
    private String message;
    private String decisionReason;
    private User processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder id(Long id) { this.id = id; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder requester(Member requester) { this.requester = requester; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder status(PromotionRequestStatus status) { this.status = status; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder requestedRole(Role requestedRole) { this.requestedRole = requestedRole; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder message(String message) { this.message = message; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder decisionReason(String decisionReason) { this.decisionReason = decisionReason; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder processedBy(User processedBy) { this.processedBy = processedBy; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    /** {@inheritDoc} */

    @Override
    public PromotionRequestBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

    /** {@inheritDoc} */
    @Override
    public PromotionRequest build() { return new PromotionRequest(this); }
}
