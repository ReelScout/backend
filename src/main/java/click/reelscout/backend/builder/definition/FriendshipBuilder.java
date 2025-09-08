package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;

import java.time.LocalDateTime;

public interface FriendshipBuilder extends EntityBuilder<Friendship, FriendshipBuilder> {
    FriendshipBuilder requester(Member requester);
    FriendshipBuilder addressee(Member addressee);
    FriendshipBuilder status(FriendshipStatus status);
    FriendshipBuilder createdAt(LocalDateTime createdAt);
    FriendshipBuilder updatedAt(LocalDateTime updatedAt);
}

