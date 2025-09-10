package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.PromotionDecisionRequestDTO;
import click.reelscout.backend.dto.request.PromotionRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

public interface PromotionService {
    // Verified member promotion (Member -> Verified)
    CustomResponseDTO requestVerifiedPromotion(Member requester, PromotionRequestCreateDTO dto);
    List<PromotionRequestResponseDTO> listPendingVerifiedRequests();
    CustomResponseDTO approveVerifiedPromotion(User moderator, Long requestId);
    CustomResponseDTO rejectVerifiedPromotion(User moderator, Long requestId, PromotionDecisionRequestDTO dto);

    // Moderator promotion (Verified -> Moderator)
    CustomResponseDTO requestModeratorPromotion(Member requester, PromotionRequestCreateDTO dto);
    List<PromotionRequestResponseDTO> listPendingModeratorRequests();
    CustomResponseDTO approveModeratorPromotion(User admin, Long requestId);
    CustomResponseDTO rejectModeratorPromotion(User admin, Long requestId, PromotionDecisionRequestDTO dto);

    // Common
    List<PromotionRequestResponseDTO> myRequests(Member requester);
}

