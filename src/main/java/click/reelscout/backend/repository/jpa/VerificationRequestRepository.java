package click.reelscout.backend.repository.jpa;

import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.VerificationRequest;
import click.reelscout.backend.model.jpa.VerificationRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {
    Optional<VerificationRequest> findByRequesterAndStatus(Member requester, VerificationRequestStatus status);

    List<VerificationRequest> findAllByStatus(VerificationRequestStatus status);

    List<VerificationRequest> findAllByRequester(Member requester);
}

