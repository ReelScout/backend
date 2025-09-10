package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.VerificationDecisionRequestDTO;
import click.reelscout.backend.dto.request.VerificationRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.VerificationRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.user}/verification")
public class VerificationController {
    private final VerificationService verificationService;

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MEMBER)")
    @PostMapping("/request")
    public ResponseEntity<CustomResponseDTO> requestVerification(@AuthenticationPrincipal Member requester,
                                                                 @Valid @RequestBody(required = false) VerificationRequestCreateDTO dto) {
        return ResponseEntity.ok(verificationService.requestVerification(requester, dto));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @GetMapping("/requests/pending")
    public ResponseEntity<List<VerificationRequestResponseDTO>> listPending() {
        return ResponseEntity.ok(verificationService.listPendingRequests());
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/requests/{id}/approve")
    public ResponseEntity<CustomResponseDTO> approve(@AuthenticationPrincipal User moderator, @PathVariable Long id) {
        return ResponseEntity.ok(verificationService.approveRequest(moderator, id));
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PatchMapping("/requests/{id}/reject")
    public ResponseEntity<CustomResponseDTO> reject(@AuthenticationPrincipal User moderator,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody(required = false) VerificationDecisionRequestDTO dto) {
        return ResponseEntity.ok(verificationService.rejectRequest(moderator, id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/requests/me")
    public ResponseEntity<List<VerificationRequestResponseDTO>> myRequests(@AuthenticationPrincipal Member requester) {
        return ResponseEntity.ok(verificationService.myRequests(requester));
    }
}

