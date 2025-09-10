package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.PromotionRequestBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "promotion_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_id", "status", "requested_role"})
})
public class PromotionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id")
    private Member requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role requestedRole;

    @Column(length = 1000)
    private String message;

    @Column(length = 1000)
    private String decisionReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by_id")
    private User processedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public PromotionRequest(PromotionRequestBuilderImplementation builder) {
        this.id = builder.getId();
        this.requester = builder.getRequester();
        this.status = builder.getStatus();
        this.requestedRole = builder.getRequestedRole();
        this.message = builder.getMessage();
        this.decisionReason = builder.getDecisionReason();
        this.processedBy = builder.getProcessedBy();
        this.createdAt = builder.getCreatedAt();
        this.updatedAt = builder.getUpdatedAt();
    }
}

