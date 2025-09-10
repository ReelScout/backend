package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ForumPostBuilder;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class ForumPostBuilderImplementation implements ForumPostBuilder {
    private Long id;
    private ForumThread thread;
    private User author;
    private ForumPost parent;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public ForumPostBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ForumPostBuilder thread(ForumThread thread) {
        this.thread = thread;
        return this;
    }

    @Override
    public ForumPostBuilder author(User author) {
        this.author = author;
        return this;
    }

    @Override
    public ForumPostBuilder parent(ForumPost parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public ForumPostBuilder body(String body) {
        this.body = body;
        return this;
    }

    @Override
    public ForumPostBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public ForumPostBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public ForumPost build() {
        return new ForumPost(this);
    }
}

