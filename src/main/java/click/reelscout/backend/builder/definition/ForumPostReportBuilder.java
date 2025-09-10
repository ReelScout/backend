package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

public interface ForumPostReportBuilder extends EntityBuilder<ForumPostReport, ForumPostReportBuilder> {
    ForumPostReportBuilder post(ForumPost post);
    ForumPostReportBuilder reporter(User reporter);
    ForumPostReportBuilder reason(String reason);
    ForumPostReportBuilder createdAt(LocalDateTime createdAt);
}

