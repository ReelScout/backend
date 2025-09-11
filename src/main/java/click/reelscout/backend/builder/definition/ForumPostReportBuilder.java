package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link ForumPostReport} instances.
 */
public interface ForumPostReportBuilder extends EntityBuilder<ForumPostReport, ForumPostReportBuilder> {
    /** Set the reported post. */
    ForumPostReportBuilder post(ForumPost post);
    /** Set the reporting user. */
    ForumPostReportBuilder reporter(User reporter);
    /** Set the reason for reporting. */
    ForumPostReportBuilder reason(String reason);
    /** Set creation timestamp. */
    ForumPostReportBuilder createdAt(LocalDateTime createdAt);
}
