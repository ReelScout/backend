package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ForumPostReportBuilder;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class ForumPostReportBuilderImplementation implements ForumPostReportBuilder {
    private Long id;
    private ForumPost post;
    private User reporter;
    private String reason;
    private LocalDateTime createdAt;

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder post(ForumPost post) {
        this.post = post;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder reporter(User reporter) {
        this.reporter = reporter;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReport build() {
        return new ForumPostReport(this);
    }
}

