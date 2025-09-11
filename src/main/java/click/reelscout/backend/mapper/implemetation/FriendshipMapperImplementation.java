package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.FriendshipWithUsersResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.FriendshipMapper;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipMapperImplementation implements FriendshipMapper {
    private final FriendshipBuilder friendshipBuilder;

    /** {@inheritDoc} */
    @Override
    public FriendshipResponseDTO toDto(Friendship friendship, UserResponseDTO requester, UserResponseDTO addressee) {
        return new FriendshipWithUsersResponseDTO(
                friendship.getId(),
                friendship.getStatus(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt(),
                requester,
                addressee
        );
    }

    /** {@inheritDoc} */
    @Override
    public FriendshipBuilder toBuilder(Friendship friendship) {
        return friendshipBuilder
                .id(friendship.getId())
                .requester(friendship.getRequester())
                .addressee(friendship.getAddressee())
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .updatedAt(friendship.getUpdatedAt());
    }

    /** {@inheritDoc} */
    @Override
    public Friendship toEntity(Member requester, Member addressee, FriendshipStatus status) {
        return friendshipBuilder
                .id(null)
                .requester(requester)
                .addressee(addressee)
                .status(status)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}

