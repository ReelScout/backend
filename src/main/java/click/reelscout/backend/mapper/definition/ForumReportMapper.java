package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ForumPostReportBuilder;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;

public interface ForumReportMapper {
    ForumPostReportBuilder toBuilder(ForumPostReport report);

    ForumPostReport toEntity(ForumPost post, User reporter, String reason);
}

