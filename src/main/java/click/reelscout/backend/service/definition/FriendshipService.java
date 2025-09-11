package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.model.jpa.Member;

import java.util.List;

/**
 * Service definition for managing friendships between members.
 * <p>
 * Provides operations for sending, accepting, rejecting friend requests,
 * removing friends and listing friend relationships and requests.
 */
public interface FriendshipService {
    /**
     * Send a friend request from the requester to the addressee identified by id.
     *
     * @param requester   the member sending the request
     * @param addresseeId the id of the member to add
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO sendRequest(Member requester, Long addresseeId);

    /**
     * Accept a pending friend request.
     *
     * @param addressee   the member accepting the request
     * @param requesterId the id of the member who requested friendship
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO acceptRequest(Member addressee, Long requesterId);

    /**
     * Reject a pending friend request.
     *
     * @param addressee   the member rejecting the request
     * @param requesterId the id of the member who requested friendship
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO rejectRequest(Member addressee, Long requesterId);

    /**
     * Remove an existing friend relationship.
     *
     * @param member   the member performing the removal
     * @param friendId the id of the friend to remove
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO removeFriend(Member member, Long friendId);

    /**
     * List all friends for a member.
     *
     * @param member the member whose friends should be returned
     * @return list of {@link FriendshipResponseDTO} representing friends
     */
    List<FriendshipResponseDTO> getFriends(Member member);

    /**
     * List incoming friend requests for a member.
     *
     * @param member the member whose incoming requests should be returned
     * @return list of {@link FriendshipResponseDTO} representing incoming requests
     */
    List<FriendshipResponseDTO> getIncomingRequests(Member member);

    /**
     * List outgoing friend requests for a member.
     *
     * @param member the member whose outgoing requests should be returned
     * @return list of {@link FriendshipResponseDTO} representing outgoing requests
     */
    List<FriendshipResponseDTO> getOutgoingRequests(Member member);
}
