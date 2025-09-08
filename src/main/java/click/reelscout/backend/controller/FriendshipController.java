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

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.friends}")
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/request/{memberId}")
    public ResponseEntity<CustomResponseDTO> sendFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.sendRequest(authenticatedMember, memberId));
    }

    @PatchMapping("/accept/{memberId}")
    public ResponseEntity<CustomResponseDTO> acceptFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.acceptRequest(authenticatedMember, memberId));
    }

    @PatchMapping("/reject/{memberId}")
    public ResponseEntity<CustomResponseDTO> rejectFriendRequest(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.rejectRequest(authenticatedMember, memberId));
    }

    @DeleteMapping("/remove/{memberId}")
    public ResponseEntity<CustomResponseDTO> removeFriend(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long memberId) {
        return ResponseEntity.ok(friendshipService.removeFriend(authenticatedMember, memberId));
    }

    @GetMapping
    public ResponseEntity<List<FriendshipResponseDTO>> getFriends(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getFriends(authenticatedMember));
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendshipResponseDTO>> getIncomingRequests(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getIncomingRequests(authenticatedMember));
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendshipResponseDTO>> getOutgoingRequests(@AuthenticationPrincipal Member authenticatedMember) {
        return ResponseEntity.ok(friendshipService.getOutgoingRequests(authenticatedMember));
    }
}
