package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;

/**
 * Mapper interface for converting between {@link Friendship} entities, builders, and DTOs.
 */
public interface FriendshipMapper {
    /**
     * Convert a {@link Friendship} entity to a {@link FriendshipResponseDTO}.
     *
     * @param friendship the friendship entity
     * @param requester  the requester user DTO
     * @param addressee  the addressee user DTO
     * @return the friendship response DTO
     */
    FriendshipResponseDTO toDto(Friendship friendship, UserResponseDTO requester, UserResponseDTO addressee);

    /**
     * Convert a {@link Friendship} entity to its builder representation.
     *
     * @param friendship the friendship entity
     * @return the corresponding builder
     */
    FriendshipBuilder toBuilder(Friendship friendship);

    /**
     * Create a {@link Friendship} entity from the given requester, addressee, and status.
     *
     * @param requester the requester member
     * @param addressee the addressee member
     * @param status    the friendship status
     * @return the created friendship entity
     */
    Friendship toEntity(Member requester, Member addressee, FriendshipStatus status);
}
