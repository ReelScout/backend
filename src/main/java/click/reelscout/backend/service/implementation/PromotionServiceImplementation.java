package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.PromotionDecisionRequestDTO;
import click.reelscout.backend.dto.request.PromotionRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.MemberMapper;
import click.reelscout.backend.mapper.definition.PromotionRequestMapper;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.repository.jpa.PromotionRequestRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.service.definition.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class PromotionServiceImplementation implements PromotionService {
    private final PromotionRequestRepository repository;
    private final UserRepository<Member> userRepository;
    private final MemberMapper memberMapper;
    private final PromotionRequestMapper mapper;

    @Override
    public CustomResponseDTO requestVerifiedPromotion(Member requester, PromotionRequestCreateDTO dto) {
        if (requester.getRole() == Role.VERIFIED_MEMBER || requester.getRole() == Role.MODERATOR || requester.getRole() == Role.ADMIN) {
            throw new EntityCreateException("User is already verified or higher");
        }
        repository.findByRequesterAndStatusAndRequestedRole(requester, PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER)
                .ifPresent(v -> { throw new EntityCreateException("There is already a pending promotion request"); });

        PromotionRequest req = mapper.toEntity(requester, dto != null ? dto.getMessage() : null, PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER);
        try {
            repository.save(req);
            return new CustomResponseDTO("Promotion request submitted");
        } catch (Exception e) {
            throw new EntityCreateException(PromotionRequest.class);
        }
    }

    @Override
    public List<PromotionRequestResponseDTO> listPendingVerifiedRequests() {
        return repository.findAllByStatusAndRequestedRole(PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER)
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public CustomResponseDTO approveVerifiedPromotion(User moderator, Long requestId) {
        PromotionRequest req = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(PromotionRequest.class));

        if (req.getStatus() != PromotionRequestStatus.PENDING) throw new EntityUpdateException("Request is not pending");
        if (req.getRequestedRole() != Role.VERIFIED_MEMBER) throw new EntityUpdateException("Invalid request type");

        try {
            PromotionRequest updatedReq = mapper.toBuilder(req)
                    .status(PromotionRequestStatus.APPROVED)
                    .processedBy(moderator)
                    .decisionReason(null)
                    .build();
            repository.save(updatedReq);

            Member updated = memberMapper.toBuilder(req.getRequester())
                    .role(Role.VERIFIED_MEMBER)
                    .build();
            userRepository.save(updated);
            return new CustomResponseDTO("Promotion request approved");
        } catch (Exception e) {
            throw new EntityUpdateException(PromotionRequest.class);
        }
    }

    @Override
    public CustomResponseDTO rejectVerifiedPromotion(User moderator, Long requestId, PromotionDecisionRequestDTO dto) {
        PromotionRequest req = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(PromotionRequest.class));
        if (req.getStatus() != PromotionRequestStatus.PENDING) throw new EntityUpdateException("Request is not pending");
        if (req.getRequestedRole() != Role.VERIFIED_MEMBER) throw new EntityUpdateException("Invalid request type");

        try {
            String reason = dto != null ? dto.getReason() : null;
            PromotionRequest updatedReq = mapper.toBuilder(req)
                    .status(PromotionRequestStatus.REJECTED)
                    .processedBy(moderator)
                    .decisionReason(reason)
                    .build();
            repository.save(updatedReq);
            return new CustomResponseDTO("Promotion request rejected");
        } catch (Exception e) {
            throw new EntityUpdateException(PromotionRequest.class);
        }
    }

    @Override
    public CustomResponseDTO requestModeratorPromotion(Member requester, PromotionRequestCreateDTO dto) {
        if (requester.getRole() == Role.MODERATOR || requester.getRole() == Role.ADMIN) {
            throw new EntityCreateException("User is already moderator or higher");
        }
        if (requester.getRole() != Role.VERIFIED_MEMBER) {
            throw new EntityCreateException("Only verified members can request moderator promotion");
        }

        repository.findByRequesterAndStatusAndRequestedRole(requester, PromotionRequestStatus.PENDING, Role.MODERATOR)
                .ifPresent(v -> { throw new EntityCreateException("There is already a pending promotion request"); });

        PromotionRequest req = mapper.toEntity(requester, dto != null ? dto.getMessage() : null, PromotionRequestStatus.PENDING, Role.MODERATOR);
        try {
            repository.save(req);
            return new CustomResponseDTO("Moderator promotion request submitted");
        } catch (Exception e) {
            throw new EntityCreateException(PromotionRequest.class);
        }
    }

    @Override
    public List<PromotionRequestResponseDTO> listPendingModeratorRequests() {
        return repository.findAllByStatusAndRequestedRole(PromotionRequestStatus.PENDING, Role.MODERATOR)
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public CustomResponseDTO approveModeratorPromotion(User admin, Long requestId) {
        PromotionRequest req = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(PromotionRequest.class));
        if (req.getStatus() != PromotionRequestStatus.PENDING) throw new EntityUpdateException("Request is not pending");
        if (req.getRequestedRole() != Role.MODERATOR) throw new EntityUpdateException("Invalid request type");

        try {
            PromotionRequest updatedReq = mapper.toBuilder(req)
                    .status(PromotionRequestStatus.APPROVED)
                    .processedBy(admin)
                    .decisionReason(null)
                    .build();
            repository.save(updatedReq);

            Member updated = memberMapper.toBuilder(req.getRequester())
                    .role(Role.MODERATOR)
                    .build();
            userRepository.save(updated);
            return new CustomResponseDTO("Moderator promotion request approved");
        } catch (Exception e) {
            throw new EntityUpdateException(PromotionRequest.class);
        }
    }

    @Override
    public CustomResponseDTO rejectModeratorPromotion(User admin, Long requestId, PromotionDecisionRequestDTO dto) {
        PromotionRequest req = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(PromotionRequest.class));
        if (req.getStatus() != PromotionRequestStatus.PENDING) throw new EntityUpdateException("Request is not pending");
        if (req.getRequestedRole() != Role.MODERATOR) throw new EntityUpdateException("Invalid request type");

        try {
            String reason = dto != null ? dto.getReason() : null;
            PromotionRequest updatedReq = mapper.toBuilder(req)
                    .status(PromotionRequestStatus.REJECTED)
                    .processedBy(admin)
                    .decisionReason(reason)
                    .build();
            repository.save(updatedReq);
            return new CustomResponseDTO("Moderator promotion request rejected");
        } catch (Exception e) {
            throw new EntityUpdateException(PromotionRequest.class);
        }
    }

    @Override
    public List<PromotionRequestResponseDTO> myRequests(Member requester) {
        return repository.findAllByRequester(requester).stream().map(mapper::toDto).toList();
    }
}

