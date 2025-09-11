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

/**
 * Controller for managing user watchlists.
 * Provides endpoints for creating, updating, deleting, and retrieving watchlists,
 * as well as adding or removing content from watchlists.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.watchlist}")
public class WatchlistController {
    private final WatchlistService watchlistService;

    /**
     * Adds a new watchlist for the authenticated member.
     * @param authenticatedMember the currently authenticated member
     * @param watchlistRequestDTO the watchlist data to add
     * @return the created watchlist
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PostMapping("/add")
    public ResponseEntity<WatchlistResponseDTO> addWatchlist(@AuthenticationPrincipal Member authenticatedMember, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.create(authenticatedMember, watchlistRequestDTO));
    }

    /**
     * Updates an existing watchlist for the authenticated member.
     * @param authenticatedMember the currently authenticated member
     * @param id the ID of the watchlist to update
     * @param watchlistRequestDTO the updated watchlist data
     * @return the updated watchlist
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PutMapping("/update/{id}")
    public ResponseEntity<WatchlistResponseDTO> updateWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.update(authenticatedMember, id, watchlistRequestDTO));
    }

    /**
     * Deletes a watchlist for the authenticated member.
     * @param authenticatedMember the currently authenticated member
     * @param id the ID of the watchlist to delete
     * @return a custom response indicating the result
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponseDTO> deleteWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.delete(authenticatedMember, id));
    }

    /**
     * Retrieves all watchlists for the authenticated member.
     * @param member the currently authenticated member
     * @return a list of watchlists
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @GetMapping("/my-watchlists")
    public ResponseEntity<List<WatchlistResponseDTO>> getWatchlists(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(watchlistService.getAllByMember(member));
    }

    /**
     * Adds content to a specific watchlist for the authenticated member.
     * @param member the currently authenticated member
     * @param watchlistId the ID of the watchlist
     * @param contentId the ID of the content to add
     * @return the updated watchlist
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PatchMapping("/{watchlistId}/add-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> addContentToWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.addContentToWatchlist(member, watchlistId, contentId));
    }

    /**
     * Removes content from a specific watchlist for the authenticated member.
     * @param member the currently authenticated member
     * @param watchlistId the ID of the watchlist
     * @param contentId the ID of the content to remove
     * @return the updated watchlist
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PatchMapping("/{watchlistId}/remove-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> removeContentFromWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.removeContentFromWatchlist(member, watchlistId, contentId));
    }

    /**
     * Retrieves all watchlists containing a specific content for the authenticated member.
     * @param member the currently authenticated member
     * @param contentId the ID of the content
     * @return a list of watchlists
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @GetMapping("/by-content/{contentId}")
    public ResponseEntity<List<WatchlistResponseDTO>> getWatchlistsByContent(@AuthenticationPrincipal Member member, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.getAllByMemberAndContent(member, contentId));
    }

    /**
     * Retrieves all public watchlists for a specific member.
     * @param memberId the ID of the member
     * @return a list of public watchlists
     */
    @GetMapping("/public/{memberId}")
    public ResponseEntity<List<WatchlistResponseDTO>> getPublicWatchlistsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(watchlistService.getAllPublicByMember(memberId));
    }

    /**
     * Retrieves a specific watchlist by its ID.
     * @param member the currently authenticated member
     * @param id the ID of the watchlist
     * @return the watchlist
     */
    @GetMapping("/{id}")
    public ResponseEntity<WatchlistResponseDTO> getWatchlistById(@AuthenticationPrincipal Member member, @PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.getById(member, id));
    }
}
