package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.VerificationRequestBuilderImplementation;
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
@Table(name = "verification_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_id", "status"})
})
public class VerificationRequest implements Serializable {
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
    private VerificationRequestStatus status;

    @Column(length = 1000)
    private String message;

    @Column(length = 1000)
    private String decisionReason; // optional reason for rejection

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by_id")
    private User processedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public VerificationRequest(VerificationRequestBuilderImplementation builder) {
        this.id = builder.getId();
        this.requester = builder.getRequester();
        this.status = builder.getStatus();
        this.message = builder.getMessage();
        this.decisionReason = builder.getDecisionReason();
        this.processedBy = builder.getProcessedBy();
        this.createdAt = builder.getCreatedAt();
        this.updatedAt = builder.getUpdatedAt();
    }
}
