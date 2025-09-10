package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumPostReportRepository extends JpaRepository<ForumPostReport, Long> {
    boolean existsByPostAndReporter(ForumPost post, User reporter);

    @Query("select count(r) > 0 from ForumPostReport r where r.post in :posts and r.reporter.role = :role")
    boolean existsByPostsAndReporterRole(@Param("posts") java.util.List<ForumPost> posts, @Param("role") Role role);

    @Query("select distinct p.author from ForumPostReport r join r.post p where r.reporter.role = click.reelscout.backend.model.jpa.Role.MODERATOR")
    <U extends User> List<U> findDistinctAuthorsReportedByModerators();
}
