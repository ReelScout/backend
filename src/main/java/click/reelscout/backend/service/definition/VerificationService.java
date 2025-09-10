package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.VerificationDecisionRequestDTO;
import click.reelscout.backend.dto.request.VerificationRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.VerificationRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

public interface VerificationService {
    CustomResponseDTO requestVerification(Member requester, VerificationRequestCreateDTO dto);

    List<VerificationRequestResponseDTO> listPendingRequests();

    CustomResponseDTO approveRequest(User moderator, Long requestId);

    CustomResponseDTO rejectRequest(User moderator, Long requestId, VerificationDecisionRequestDTO dto);

    List<VerificationRequestResponseDTO> myRequests(Member requester);
}

