package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ForumPostReportBuilder;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;

/**
 * Mapper interface for converting between {@link ForumPostReport} entities and builders.
 * <p>
 * This interface provides methods to:
 * <ul>
 *     <li>Convert a {@link ForumPostReport} entity to its builder representation.</li>
 *     <li>Create a {@link ForumPostReport} entity from a forum post, reporter, and reason.</li>
 * </ul>
 */
public interface ForumReportMapper {
    /**
     * Convert a {@link ForumPostReport} entity to its builder representation.
     *
     * @param report the forum post report entity
     * @return the corresponding builder
     */
    ForumPostReportBuilder toBuilder(ForumPostReport report);

    /**
     * Create a {@link ForumPostReport} entity from the given post, reporter, and reason.
     *
     * @param post     the reported forum post
     * @param reporter the user reporting the post
     * @param reason   the reason for reporting
     * @return the created forum post report entity
     */
    ForumPostReport toEntity(ForumPost post, User reporter, String reason);
}
