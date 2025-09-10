package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;

import java.time.LocalDateTime;

public interface ForumThreadBuilder extends EntityBuilder<ForumThread, ForumThreadBuilder> {
    ForumThreadBuilder content(Content content);
    ForumThreadBuilder title(String title);
    ForumThreadBuilder createdBy(User createdBy);
    ForumThreadBuilder createdAt(LocalDateTime createdAt);
    ForumThreadBuilder updatedAt(LocalDateTime updatedAt);
}

