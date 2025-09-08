package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.watchlist}")
public class WatchlistController {
    private final WatchlistService watchlistService;

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PostMapping("/add")
    public ResponseEntity<WatchlistResponseDTO> addWatchlist(@AuthenticationPrincipal Member authenticatedMember, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.create(authenticatedMember, watchlistRequestDTO));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PutMapping("/update/{id}")
    public ResponseEntity<WatchlistResponseDTO> updateWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.update(authenticatedMember, id, watchlistRequestDTO));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponseDTO> deleteWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.delete(authenticatedMember, id));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @GetMapping("/my-watchlists")
    public ResponseEntity<List<WatchlistResponseDTO>> getWatchlists(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(watchlistService.getAllByMember(member));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PatchMapping("/{watchlistId}/add-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> addContentToWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.addContentToWatchlist(member, watchlistId, contentId));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PatchMapping("/{watchlistId}/remove-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> removeContentFromWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.removeContentFromWatchlist(member, watchlistId, contentId));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @GetMapping("/by-content/{contentId}")
    public ResponseEntity<List<WatchlistResponseDTO>> getWatchlistsByContent(@AuthenticationPrincipal Member member, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.getAllByMemberAndContent(member, contentId));
    }

    @GetMapping("/public/{memberId}")
    public ResponseEntity<List<WatchlistResponseDTO>> getPublicWatchlistsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(watchlistService.getAllPublicByMember(memberId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WatchlistResponseDTO> getWatchlistById(@AuthenticationPrincipal Member member, @PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.getById(member, id));
    }
}
