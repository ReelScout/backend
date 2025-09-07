package click.reelscout.backend.model.jpa;

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

    public ForumPost(ForumThread thread, User author, ForumPost parent, String body) {
        this.thread = thread;
        this.author = author;
        this.parent = parent;
        this.body = body;
    }
}

