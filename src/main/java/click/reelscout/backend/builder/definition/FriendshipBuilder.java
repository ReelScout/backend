package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link Friendship} instances.
 */
public interface FriendshipBuilder extends EntityBuilder<Friendship, FriendshipBuilder> {
    /** Set the requester member. */
    FriendshipBuilder requester(Member requester);
    /** Set the addressee member. */
    FriendshipBuilder addressee(Member addressee);
    /** Set the friendship status. */
    FriendshipBuilder status(FriendshipStatus status);
    /** Set creation timestamp. */
    FriendshipBuilder createdAt(LocalDateTime createdAt);
    /** Set update timestamp. */
    FriendshipBuilder updatedAt(LocalDateTime updatedAt);
}
