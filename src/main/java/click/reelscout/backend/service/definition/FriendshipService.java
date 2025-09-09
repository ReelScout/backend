package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.model.jpa.Member;

import java.util.List;

public interface FriendshipService {
    CustomResponseDTO sendRequest(Member requester, Long addresseeId);

    CustomResponseDTO acceptRequest(Member addressee, Long requesterId);

    CustomResponseDTO rejectRequest(Member addressee, Long requesterId);

    CustomResponseDTO removeFriend(Member member, Long friendId);

    List<FriendshipResponseDTO> getFriends(Member member);

    List<FriendshipResponseDTO> getIncomingRequests(Member member);

    List<FriendshipResponseDTO> getOutgoingRequests(Member member);
}
