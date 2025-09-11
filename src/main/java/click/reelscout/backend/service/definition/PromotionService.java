package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.PromotionDecisionRequestDTO;
import click.reelscout.backend.dto.request.PromotionRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

/**
 * Service definition for handling promotion requests and workflows.
 * <p>
 * Supports requests for verified and moderator promotions, approval/rejection
 * flows and listing of requests.
 */
public interface PromotionService {
    /**
     * Request promotion to verified member role.
     *
     * @param requester the member requesting promotion
     * @param dto       optional promotion request data
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO requestVerifiedPromotion(Member requester, PromotionRequestCreateDTO dto);

    /**
     * List pending promotion requests targeting verified role.
     *
     * @return list of pending {@link PromotionRequestResponseDTO}
     */
    List<PromotionRequestResponseDTO> listPendingVerifiedRequests();

    /**
     * Approve a verified-promotion request.
     *
     * @param moderator the moderator performing the approval
     * @param requestId the id of the promotion request
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO approveVerifiedPromotion(User moderator, Long requestId);

    /**
     * Reject a verified-promotion request.
     *
     * @param moderator the moderator performing the rejection
     * @param requestId the id of the promotion request
     * @param dto       decision details
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO rejectVerifiedPromotion(User moderator, Long requestId, PromotionDecisionRequestDTO dto);

    /**
     * Request promotion to moderator role.
     *
     * @param requester the member requesting promotion
     * @param dto       optional promotion request data
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO requestModeratorPromotion(Member requester, PromotionRequestCreateDTO dto);

    /**
     * List pending promotion requests targeting moderator role.
     *
     * @return list of pending {@link PromotionRequestResponseDTO}
     */
    List<PromotionRequestResponseDTO> listPendingModeratorRequests();

    /**
     * Approve a moderator-promotion request.
     *
     * @param admin     the admin performing the approval
     * @param requestId the id of the promotion request
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO approveModeratorPromotion(User admin, Long requestId);

    /**
     * Reject a moderator-promotion request.
     *
     * @param admin     the admin performing the rejection
     * @param requestId the id of the promotion request
     * @param dto       decision details
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO rejectModeratorPromotion(User admin, Long requestId, PromotionDecisionRequestDTO dto);

    /**
     * List promotion requests submitted by a specific member.
     *
     * @param requester the member who submitted requests
     * @return list of {@link PromotionRequestResponseDTO}
     */
    List<PromotionRequestResponseDTO> myRequests(Member requester);
}
