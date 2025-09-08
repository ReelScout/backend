package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;

public interface FriendshipMapper {
    FriendshipResponseDTO toDto(Friendship friendship, UserResponseDTO requester, UserResponseDTO addressee);

    FriendshipBuilder toBuilder(Friendship friendship);

    Friendship toEntity(Member requester, Member addressee, FriendshipStatus status);
}

