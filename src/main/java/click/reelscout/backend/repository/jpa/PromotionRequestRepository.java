package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, Long> {
    Optional<PromotionRequest> findByRequesterAndStatusAndRequestedRole(Member requester, PromotionRequestStatus status, Role requestedRole);

    List<PromotionRequest> findAllByStatusAndRequestedRole(PromotionRequestStatus status, Role requestedRole);

    List<PromotionRequest> findAllByRequester(Member requester);
}

