package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing ForumPost entities.
 */
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    /**
     * Finds all forum posts associated with a specific thread, ordered by creation date in ascending order.
     *
     * @param thread the forum thread
     * @return a list of forum posts
     */
    List<ForumPost> findAllByThreadOrderByCreatedAtAsc(ForumThread thread);

    /**
     * Counts the number of forum posts associated with a specific thread.
     *
     * @param thread the forum thread
     * @return the count of forum posts
     */
    long countByThread(ForumThread thread);

    /**
     * Deletes all forum posts associated with a specific thread.
     *
     * @param thread the forum thread
     */
    void deleteByThread(ForumThread thread);

    /**
     * Finds all forum posts that have a specific parent post.
     *
     * @param parent the parent forum post
     * @return a list of forum posts
     */
    List<ForumPost> findAllByParent(ForumPost parent);

    /**
     * Finds all forum posts authored by a specific user.
     *
     * @param author the user who authored the posts
     * @return a list of forum posts
     */
    List<ForumPost> findAllByAuthor(User author);

    /**
     * Finds all forum posts associated with any of the specified threads.
     *
     * @param threads the list of forum threads
     * @return a list of forum posts
     */
    List<ForumPost> findAllByThreadIn(List<ForumThread> threads);
}
