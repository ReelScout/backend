package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.FriendshipBuilderImplementation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a friendship relationship between two members.
 */
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"addressee_id", "requester_id"})
})
@Entity
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Friendship implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn
    private Member requester;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Member addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Friendship(FriendshipBuilderImplementation builder) {
        this.id = builder.getId();
        this.requester = builder.getRequester();
        this.addressee = builder.getAddressee();
        this.status = builder.getStatus();
        this.createdAt = builder.getCreatedAt();
        this.updatedAt = builder.getUpdatedAt();
    }

}
