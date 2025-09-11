package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing ForumThread entities.
 */
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    /**
     * Finds all forum threads associated with the given content.
     *
     * @param content the content entity
     * @return a list of forum threads associated with the content
     */
    List<ForumThread> findAllByContent(Content content);

    /**
     * Finds all forum threads associated with any of the given contents.
     *
     * @param contents a list of content entities
     * @return a list of forum threads associated with the contents
     */
    List<ForumThread> findAllByContentIn(List<Content> contents);
}
