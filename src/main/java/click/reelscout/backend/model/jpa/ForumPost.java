package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.ForumPostBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a post in a forum thread.
 */
@Entity
@Getter
@NoArgsConstructor
public class ForumPost implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ForumThread thread;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    private ForumPost parent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ForumPostReport> reports;

    public ForumPost(ForumPostBuilderImplementation b) {
        this.id = b.getId();
        this.thread = b.getThread();
        this.author = b.getAuthor();
        this.parent = b.getParent();
        this.body = b.getBody();
        this.createdAt = b.getCreatedAt();
        this.updatedAt = b.getUpdatedAt();
        this.reports = new ArrayList<>();
    }
}
