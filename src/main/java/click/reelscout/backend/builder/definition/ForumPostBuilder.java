package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

public interface ForumPostBuilder extends EntityBuilder<ForumPost, ForumPostBuilder> {
    ForumPostBuilder thread(ForumThread thread);
    ForumPostBuilder author(User author);
    ForumPostBuilder parent(ForumPost parent);
    ForumPostBuilder body(String body);
    ForumPostBuilder createdAt(LocalDateTime createdAt);
    ForumPostBuilder updatedAt(LocalDateTime updatedAt);
}

