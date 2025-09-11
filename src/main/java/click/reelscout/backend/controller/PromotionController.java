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

/**
 * Controller for handling promotion requests and decisions.
 * Supports requesting for role upgrades and approving or rejecting promotion requests.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.user}/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    /**
     * Endpoint for members to request a promotion to Verified.
     *
     * @param requester the member requesting promotion
     * @param dto optional DTO containing promotion request details
     * @return details of the operation result wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PostMapping("/verified/request")
    public ResponseEntity<CustomResponseDTO> requestVerified(@AuthenticationPrincipal Member requester,
                                                             @Valid @RequestBody(required = false) PromotionRequestCreateDTO dto) {
        return ResponseEntity.ok(promotionService.requestVerifiedPromotion(requester, dto));
    }

    /**
     * Endpoint for moderators to list pending Verified promotion requests.
     *
     * @return list of PromotionRequestResponseDTO for pending Verified requests
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @GetMapping("/verified/requests/pending")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listPendingVerified() {
        return ResponseEntity.ok(promotionService.listPendingVerifiedRequests());
    }

    /**
     * Endpoint for moderators to approve a Verified promotion request.
     *
     * @param moderator the moderator approving the request
     * @param id the ID of the promotion request
     * @return details of the approval operation wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/verified/requests/{id}/approve")
    public ResponseEntity<CustomResponseDTO> approveVerified(@AuthenticationPrincipal User moderator, @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.approveVerifiedPromotion(moderator, id));
    }

    /**
     * Endpoint for moderators to reject a Verified promotion request.
     *
     * @param moderator the moderator rejecting the request
     * @param id the ID of the promotion request
     * @param dto optional DTO containing details for the decision
     * @return details of the rejection operation wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/verified/requests/{id}/reject")
    public ResponseEntity<CustomResponseDTO> rejectVerified(@AuthenticationPrincipal User moderator,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody(required = false) PromotionDecisionRequestDTO dto) {
        return ResponseEntity.ok(promotionService.rejectVerifiedPromotion(moderator, id, dto));
    }

    /**
     * Endpoint for verified members to request a promotion to Moderator.
     *
     * @param requester the verified member requesting promotion
     * @param dto optional DTO containing promotion request details
     * @return details of the operation result wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).VERIFIED_MEMBER)")
    @PostMapping("/moderator/request")
    public ResponseEntity<CustomResponseDTO> requestModerator(@AuthenticationPrincipal Member requester,
                                                              @Valid @RequestBody(required = false) PromotionRequestCreateDTO dto) {
        return ResponseEntity.ok(promotionService.requestModeratorPromotion(requester, dto));
    }

    /**
     * Endpoint for admins to list pending Moderator promotion requests.
     *
     * @return list of PromotionRequestResponseDTO for pending Moderator requests
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @GetMapping("/moderator/requests/pending")
    public ResponseEntity<List<PromotionRequestResponseDTO>> listPendingModerator() {
        return ResponseEntity.ok(promotionService.listPendingModeratorRequests());
    }

    /**
     * Endpoint for admins to approve a Moderator promotion request.
     *
     * @param admin the admin approving the request
     * @param id the ID of the promotion request
     * @return details of the approval operation wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @PatchMapping("/moderator/requests/{id}/approve")
    public ResponseEntity<CustomResponseDTO> approveModerator(@AuthenticationPrincipal User admin, @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.approveModeratorPromotion(admin, id));
    }

    /**
     * Endpoint for admins to reject a Moderator promotion request.
     *
     * @param admin the admin rejecting the request
     * @param id the ID of the promotion request
     * @param dto optional DTO containing details for the decision
     * @return details of the rejection operation wrapped in a CustomResponseDTO
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @PatchMapping("/moderator/requests/{id}/reject")
    public ResponseEntity<CustomResponseDTO> rejectModerator(@AuthenticationPrincipal User admin,
                                                             @PathVariable Long id,
                                                             @Valid @RequestBody(required = false) PromotionDecisionRequestDTO dto) {
        return ResponseEntity.ok(promotionService.rejectModeratorPromotion(admin, id, dto));
    }

    /**
     * Endpoint to retrieve the promotion requests made by the authenticated member.
     *
     * @param requester the member whose requests are to be retrieved
     * @return list of PromotionRequestResponseDTO representing the member's promotion requests
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/requests/me")
    public ResponseEntity<List<PromotionRequestResponseDTO>> myRequests(@AuthenticationPrincipal Member requester) {
        return ResponseEntity.ok(promotionService.myRequests(requester));
    }
}
