package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class FriendshipBuilderImplementation implements FriendshipBuilder {
    private Long id;
    private Member requester;
    private Member addressee;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder requester(Member requester) {
        this.requester = requester;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder addressee(Member addressee) {
        this.addressee = addressee;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder status(FriendshipStatus status) {
        this.status = status;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Friendship build() {
        return new Friendship(this);
    }
}
