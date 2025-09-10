package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.VerificationDecisionRequestDTO;
import click.reelscout.backend.dto.request.VerificationRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.VerificationRequestResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.mapper.definition.VerificationRequestMapper;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.repository.jpa.VerificationRequestRepository;
import click.reelscout.backend.service.definition.VerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class VerificationServiceImplementation implements VerificationService {
    private final VerificationRequestRepository verificationRequestRepository;
    private final UserRepository<Member> userRepository;
    private final MemberMapper memberMapper;
    private final VerificationRequestMapper verificationRequestMapper;

    @Override
    public CustomResponseDTO requestVerification(Member requester, VerificationRequestCreateDTO dto) {
        if (requester.getRole() == Role.VERIFIED_MEMBER || requester.getRole() == Role.MODERATOR || requester.getRole() == Role.ADMIN) {
            throw new EntityCreateException("User is already verified or higher");
        }

        verificationRequestRepository.findByRequesterAndStatus(requester, VerificationRequestStatus.PENDING)
                .ifPresent(v -> { throw new EntityCreateException("There is already a pending verification request"); });

        VerificationRequest request = verificationRequestMapper.toEntity(
                requester,
                dto != null ? dto.getMessage() : null,
                VerificationRequestStatus.PENDING
        );
        try {
            verificationRequestRepository.save(request);
            return new CustomResponseDTO("Verification request submitted");
        } catch (Exception e) {
            throw new EntityCreateException(VerificationRequest.class);
        }
    }

    @Override
    public List<VerificationRequestResponseDTO> listPendingRequests() {
        return verificationRequestRepository.findAllByStatus(VerificationRequestStatus.PENDING)
                .stream().map(verificationRequestMapper::toDto).toList();
    }

    @Override
    public CustomResponseDTO approveRequest(User moderator, Long requestId) {
        VerificationRequest request = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(VerificationRequest.class));

        if (request.getStatus() != VerificationRequestStatus.PENDING) {
            throw new EntityUpdateException("Request is not pending");
        }

        try {
            VerificationRequest updatedReq = verificationRequestMapper.toBuilder(request)
                    .status(VerificationRequestStatus.APPROVED)
                    .processedBy(moderator)
                    .decisionReason(null)
                    .build();

            verificationRequestRepository.save(updatedReq);

            Member requester = request.getRequester();
            Member updated = memberMapper.toBuilder(requester)
                    .role(Role.VERIFIED_MEMBER)
                    .build();
            userRepository.save(updated);

            return new CustomResponseDTO("Verification request approved");
        } catch (Exception e) {
            throw new EntityUpdateException(VerificationRequest.class);
        }
    }

    @Override
    public CustomResponseDTO rejectRequest(User moderator, Long requestId, VerificationDecisionRequestDTO dto) {
        VerificationRequest request = verificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(VerificationRequest.class));

        if (request.getStatus() != VerificationRequestStatus.PENDING) {
            throw new EntityUpdateException("Request is not pending");
        }

        try {
            String reason = dto != null ? dto.getReason() : null;
            VerificationRequest updatedReq = verificationRequestMapper.toBuilder(request)
                    .status(VerificationRequestStatus.REJECTED)
                    .processedBy(moderator)
                    .decisionReason(reason)
                    .build();
            verificationRequestRepository.save(updatedReq);
            return new CustomResponseDTO("Verification request rejected");
        } catch (Exception e) {
            throw new EntityUpdateException(VerificationRequest.class);
        }
    }

    @Override
    public List<VerificationRequestResponseDTO> myRequests(Member requester) {
        return verificationRequestRepository.findAllByRequester(requester)
                .stream().map(verificationRequestMapper::toDto).toList();
    }
}
