package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing ForumPostReport entities.
 * Provides methods for handling reports on forum posts.
 */
public interface ForumPostReportRepository extends JpaRepository<ForumPostReport, Long> {

    /**
     * Checks if a report exists for a specific post by a specific reporter.
     *
     * @param post the forum post being reported
     * @param reporter the user reporting the post
     * @return true if the report exists, false otherwise
     */
    boolean existsByPostAndReporter(ForumPost post, User reporter);

    /**
     * Checks if reports exist for a list of posts by reporters with a specific role.
     *
     * @param posts the list of forum posts
     * @param role the role of the reporters
     * @return true if such reports exist, false otherwise
     */
    @Query("select count(r) > 0 from ForumPostReport r where r.post in :posts and r.reporter.role = :role")
    boolean existsByPostsAndReporterRole(@Param("posts") java.util.List<ForumPost> posts, @Param("role") Role role);

    /**
     * Finds distinct authors of posts reported by moderators.
     *
     * @param <U> the type of user
     * @return a list of distinct authors reported by moderators
     */
    @Query("select distinct p.author from ForumPostReport r join r.post p where r.reporter.role = click.reelscout.backend.model.jpa.Role.MODERATOR")
    <U extends User> List<U> findDistinctAuthorsReportedByModerators();

    /**
     * Finds all reports for a list of forum posts.
     *
     * @param posts the list of forum posts
     * @return a list of forum post reports
     */
    List<ForumPostReport> findAllByPostIn(List<ForumPost> posts);
}
