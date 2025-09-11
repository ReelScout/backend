package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing PromotionRequest entities.
 * Provides methods for retrieving and managing promotion requests.
 */
public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, Long> {

    /**
     * Finds a promotion request by requester, status, and requested role.
     *
     * @param requester the member requesting the promotion
     * @param status the status of the promotion request
     * @param requestedRole the role being requested
     * @return an optional containing the promotion request if found
     */
    Optional<PromotionRequest> findByRequesterAndStatusAndRequestedRole(Member requester, PromotionRequestStatus status, Role requestedRole);

    /**
     * Retrieves all promotion requests by status and requested role.
     *
     * @param status the status of the promotion requests
     * @param requestedRole the role being requested
     * @return a list of matching promotion requests
     */
    List<PromotionRequest> findAllByStatusAndRequestedRole(PromotionRequestStatus status, Role requestedRole);

    /**
     * Retrieves all promotion requests made by a specific requester.
     *
     * @param requester the member who made the requests
     * @return a list of promotion requests by the requester
     */
    List<PromotionRequest> findAllByRequester(Member requester);
}
