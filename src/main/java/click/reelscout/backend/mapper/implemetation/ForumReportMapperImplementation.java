package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ForumPostReportBuilder;
import click.reelscout.backend.mapper.definition.ForumReportMapper;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ForumReportMapper} for mapping forum post reports.
 */
@Component
@RequiredArgsConstructor
public class ForumReportMapperImplementation implements ForumReportMapper {
    private final ForumPostReportBuilder reportBuilder;

    /** {@inheritDoc} */
    @Override
    public ForumPostReportBuilder toBuilder(ForumPostReport report) {
        return reportBuilder
                .id(report.getId())
                .post(report.getPost())
                .reporter(report.getReporter())
                .reason(report.getReason())
                .createdAt(report.getCreatedAt());
    }

    /** {@inheritDoc} */
    @Override
    public ForumPostReport toEntity(ForumPost post, User reporter, String reason) {
        return reportBuilder
                .id(null)
                .post(post)
                .reporter(reporter)
                .reason(reason)
                .createdAt(null)
                .build();
    }
}
