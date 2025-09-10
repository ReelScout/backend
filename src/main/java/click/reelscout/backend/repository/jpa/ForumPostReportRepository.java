package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumPostReportRepository extends JpaRepository<ForumPostReport, Long> {
    boolean existsByPostAndReporter(ForumPost post, User reporter);
}

