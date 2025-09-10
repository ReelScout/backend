package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.ForumPostReportBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_reporter", columnNames = {"post_id", "reporter_id"})
})
public class ForumPostReport implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ForumPost post;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public ForumPostReport(ForumPostReportBuilderImplementation b) {
        this.id = b.getId();
        this.post = b.getPost();
        this.reporter = b.getReporter();
        this.reason = b.getReason();
        this.createdAt = b.getCreatedAt();
    }
}
