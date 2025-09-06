package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.user}/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;

    @PostMapping("/add")
    public ResponseEntity<WatchlistResponseDTO> addWatchlist(@AuthenticationPrincipal Member authenticatedMember, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.create(authenticatedMember, watchlistRequestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<WatchlistResponseDTO> updateWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id, @RequestBody WatchlistRequestDTO watchlistRequestDTO) {
        return ResponseEntity.ok(watchlistService.update(authenticatedMember, id, watchlistRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponseDTO> deleteWatchlist(@AuthenticationPrincipal Member authenticatedMember, @PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.delete(authenticatedMember, id));
    }

    @GetMapping("/my-watchlists")
    public ResponseEntity<List<WatchlistResponseDTO>> getWatchlists(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(watchlistService.getByMember(member));
    }

    @PatchMapping("/{watchlistId}/add-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> addContentToWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.addContentToWatchlist(member, watchlistId, contentId));
    }

    @PatchMapping("/{watchlistId}/remove-content/{contentId}")
    public ResponseEntity<WatchlistResponseDTO> removeContentFromWatchlist(@AuthenticationPrincipal Member member, @PathVariable Long watchlistId, @PathVariable Long contentId) {
        return ResponseEntity.ok(watchlistService.removeContentFromWatchlist(member, watchlistId, contentId));
    }
}
