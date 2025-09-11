package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing friendship operations such as sending requests,
 * accepting, rejecting, and listing friendships.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.friends}")
public class FriendshipController {
    private final FriendshipService friendshipService;

    /**
     * Sends a friend request from the authenticated member to the specified member.
     *
     * @param authenticatedMember the currently authenticated member
     * @param memberId the ID of the member to send a friend request to
     * @return a ResponseEntity containing the result of the friend request operation
     */
    @PostMapping("/request/{memberId}")
    public ResponseEntity<CustomResponseDTO> sendFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.sendRequest(authenticatedMember, memberId));
    }

    /**
     * Accepts a friend request from the member with the given ID.
     *
     * @param authenticatedMember the currently authenticated member
     * @param memberId the ID of the member whose friend request is being accepted
     * @return a ResponseEntity containing the result of the accept operation
     */
    @PatchMapping("/accept/{memberId}")
    public ResponseEntity<CustomResponseDTO> acceptFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.acceptRequest(authenticatedMember, memberId));
    }

    /**
     * Rejects a friend request from the member with the given ID.
     *
     * @param authenticatedMember the currently authenticated member
     * @param memberId the ID of the member whose friend request is being rejected
     * @return a ResponseEntity containing the result of the reject operation
     */
    @PatchMapping("/reject/{memberId}")
    public ResponseEntity<CustomResponseDTO> rejectFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.rejectRequest(authenticatedMember, memberId));
    }

    /**
     * Removes a friend with the specified member ID from the authenticated member's friend list.
     *
     * @param authenticatedMember the currently authenticated member
     * @param memberId the ID of the member to remove from friends
     * @return a ResponseEntity containing the result of the remove operation
     */
    @DeleteMapping("/remove/{memberId}")
    public ResponseEntity<CustomResponseDTO> removeFriend(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.removeFriend(authenticatedMember, memberId));
    }

    /**
     * Retrieves the list of friends for the authenticated member.
     *
     * @param authenticatedMember the currently authenticated member
     * @return a ResponseEntity containing the list of FriendshipResponseDTOs
     */
    @GetMapping
    public ResponseEntity<List<FriendshipResponseDTO>> getFriends(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getFriends(authenticatedMember));
    }

    /**
     * Retrieves the list of incoming friend requests for the authenticated member.
     *
     * @param authenticatedMember the currently authenticated member
     * @return a ResponseEntity containing the list of incoming FriendshipResponseDTOs
     */
    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendshipResponseDTO>> getIncomingRequests(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getIncomingRequests(authenticatedMember));
    }

    /**
     * Retrieves the list of outgoing friend requests made by the authenticated member.
     *
     * @param authenticatedMember the currently authenticated member
     * @return a ResponseEntity containing the list of outgoing FriendshipResponseDTOs
     */
    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendshipResponseDTO>> getOutgoingRequests(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getOutgoingRequests(authenticatedMember));
    }
}
