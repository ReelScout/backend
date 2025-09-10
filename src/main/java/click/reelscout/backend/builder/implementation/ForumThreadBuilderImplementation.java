package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ForumThreadBuilder;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class ForumThreadBuilderImplementation implements ForumThreadBuilder {
    private Long id;
    private Content content;
    private String title;
    private User createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public ForumThreadBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ForumThreadBuilder content(Content content) {
        this.content = content;
        return this;
    }

    @Override
    public ForumThreadBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ForumThreadBuilder createdBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @Override
    public ForumThreadBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public ForumThreadBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public ForumThread build() {
        return new ForumThread(this);
    }
}

