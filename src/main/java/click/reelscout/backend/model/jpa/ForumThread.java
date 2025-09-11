package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.ForumThreadBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a forum thread created by a user.
 */
@Entity
@Getter
@NoArgsConstructor
public class ForumThread implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Content content;

    @Column(nullable = false)
    private String title;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ForumThread(ForumThreadBuilderImplementation b) {
        this.id = b.getId();
        this.content = b.getContent();
        this.title = b.getTitle();
        this.createdBy = b.getCreatedBy();
        this.createdAt = b.getCreatedAt();
        this.updatedAt = b.getUpdatedAt();
    }
}
