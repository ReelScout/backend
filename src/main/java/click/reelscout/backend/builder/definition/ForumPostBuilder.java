package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link ForumPost} instances.
 * <p>
 * Provides fluent setters for thread, author, parent post, body and timestamps.
 */
public interface ForumPostBuilder extends EntityBuilder<ForumPost, ForumPostBuilder> {
    /** Set the thread this post belongs to. */
    ForumPostBuilder thread(ForumThread thread);
    /** Set the author of the post. */
    ForumPostBuilder author(User author);
    /** Set the parent post (for replies). */
    ForumPostBuilder parent(ForumPost parent);
    /** Set the post body. */
    ForumPostBuilder body(String body);
    /** Set creation timestamp. */
    ForumPostBuilder createdAt(LocalDateTime createdAt);
    /** Set update timestamp. */
    ForumPostBuilder updatedAt(LocalDateTime updatedAt);
}
