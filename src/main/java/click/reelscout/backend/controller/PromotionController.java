package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.PromotionDecisionRequestDTO;
import click.reelscout.backend.dto.request.PromotionRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.user}/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    // Member -> Verified
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PostMapping("/verified/request")
    public ResponseEntity<CustomResponseDTO> requestVerified(@AuthenticationPrincipal Member requester,
                                                             @Valid @RequestBody(required = false) PromotionRequestCreateDTO dto) {
        return ResponseEntity.ok(promotionService.requestVerifiedPromotion(requester, dto));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @GetMapping("/verified/requests/pending")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listPendingVerified() {
        return ResponseEntity.ok(promotionService.listPendingVerifiedRequests());
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/verified/requests/{id}/approve")
    public ResponseEntity<CustomResponseDTO> approveVerified(@AuthenticationPrincipal User moderator, @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.approveVerifiedPromotion(moderator, id));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/verified/requests/{id}/reject")
    public ResponseEntity<CustomResponseDTO> rejectVerified(@AuthenticationPrincipal User moderator,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody(required = false) PromotionDecisionRequestDTO dto) {
        return ResponseEntity.ok(promotionService.rejectVerifiedPromotion(moderator, id, dto));
    }

    // Verified -> Moderator
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).VERIFIED_MEMBER)")
    @PostMapping("/moderator/request")
    public ResponseEntity<CustomResponseDTO> requestModerator(@AuthenticationPrincipal Member requester,
                                                              @Valid @RequestBody(required = false) PromotionRequestCreateDTO dto) {
        return ResponseEntity.ok(promotionService.requestModeratorPromotion(requester, dto));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @GetMapping("/moderator/requests/pending")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listPendingModerator() {
        return ResponseEntity.ok(promotionService.listPendingModeratorRequests());
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @PatchMapping("/moderator/requests/{id}/approve")
    public ResponseEntity<CustomResponseDTO> approveModerator(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.approveModeratorPromotion(admin, id));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @PatchMapping("/moderator/requests/{id}/reject")
    public ResponseEntity<CustomResponseDTO> rejectModerator(@AuthenticationPrincipal User admin,
                                                             @PathVariable Long id,
                                                             @Valid @RequestBody(required = false) PromotionDecisionRequestDTO dto) {
        return ResponseEntity.ok(promotionService.rejectModeratorPromotion(admin, id, dto));
    }

    // Common
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/requests/me")
    public ResponseEntity<List<PromotionRequestResponseDTO>> myRequests(@AuthenticationPrincipal Member requester) {
        return ResponseEntity.ok(promotionService.myRequests(requester));
    }
}

