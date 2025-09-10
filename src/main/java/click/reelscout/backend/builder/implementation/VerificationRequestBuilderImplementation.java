package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.VerificationRequestBuilder;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.model.jpa.VerificationRequest;
import click.reelscout.backend.model.jpa.VerificationRequestStatus;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class VerificationRequestBuilderImplementation implements VerificationRequestBuilder {
    private Long id;
    private Member requester;
    private VerificationRequestStatus status;
    private String message;
    private String decisionReason;
    private User processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public VerificationRequestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public VerificationRequestBuilder requester(Member requester) {
        this.requester = requester;
        return this;
    }

    @Override
    public VerificationRequestBuilder status(VerificationRequestStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public VerificationRequestBuilder message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public VerificationRequestBuilder decisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
        return this;
    }

    @Override
    public VerificationRequestBuilder processedBy(User processedBy) {
        this.processedBy = processedBy;
        return this;
    }

    @Override
    public VerificationRequestBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public VerificationRequestBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public VerificationRequest build() {
        return new VerificationRequest(this);
    }
}

