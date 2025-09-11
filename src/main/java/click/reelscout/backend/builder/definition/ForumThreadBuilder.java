package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link ForumThread} instances.
 */
public interface ForumThreadBuilder extends EntityBuilder<ForumThread, ForumThreadBuilder> {
    /** Set the related content. */
    ForumThreadBuilder content(Content content);
    /** Set the thread title. */
    ForumThreadBuilder title(String title);
    /** Set the user who created the thread. */
    ForumThreadBuilder createdBy(User createdBy);
    /** Set creation timestamp. */
    ForumThreadBuilder createdAt(LocalDateTime createdAt);
    /** Set update timestamp. */
    ForumThreadBuilder updatedAt(LocalDateTime updatedAt);
}
