package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.model.jpa.VerificationRequest;
import click.reelscout.backend.model.jpa.VerificationRequestStatus;

import java.time.LocalDateTime;

public interface VerificationRequestBuilder extends EntityBuilder<VerificationRequest, VerificationRequestBuilder> {
    VerificationRequestBuilder requester(Member requester);
    VerificationRequestBuilder status(VerificationRequestStatus status);
    VerificationRequestBuilder message(String message);
    VerificationRequestBuilder decisionReason(String decisionReason);
    VerificationRequestBuilder processedBy(User processedBy);
    VerificationRequestBuilder createdAt(LocalDateTime createdAt);
    VerificationRequestBuilder updatedAt(LocalDateTime updatedAt);
}

