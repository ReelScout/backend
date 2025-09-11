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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for PromotionServiceImplementation with mocked collaborators.
 */
@ExtendWith(MockitoExtension.class)
class PromotionServiceImplementationTest {

    @Mock private PromotionRequestRepository repository;
    @Mock private UserRepository<Member> userRepository;
    @Mock private MemberMapper memberMapper;
    @Mock private PromotionRequestMapper mapper;

    private PromotionServiceImplementation service;

    @BeforeEach
    void setUp() {
        service = new PromotionServiceImplementation(repository, userRepository, memberMapper, mapper);
    }

    // ---------- requestVerifiedPromotion ----------

    @Test
    void requestVerifiedPromotion_throws_ifAlreadyVerifiedOrHigher() {
        // Arrange: requester already verified
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.VERIFIED_MEMBER);

        // Act + Assert
        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        assertThrows(EntityCreateException.class, () -> service.requestVerifiedPromotion(requester, dto));
        verifyNoInteractions(mapper);
    }

    @Test
    void requestVerifiedPromotion_throws_ifPendingAlreadyExists() {
        // Arrange: requester is regular MEMBER
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.MEMBER);

        when(repository.findByRequesterAndStatusAndRequestedRole(
                requester, PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER)
        ).thenReturn(Optional.of(new PromotionRequest()));

        // Act + Assert
        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        assertThrows(EntityCreateException.class, () -> service.requestVerifiedPromotion(requester, dto));
        verify(mapper, never()).toEntity(any(), any(), any(), any());
    }

    @Test
    void requestVerifiedPromotion_saves_andReturnsMessage_onHappyPath() {
        // Arrange
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.MEMBER);

        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        dto.setMessage("please");

        PromotionRequest entity = new PromotionRequest();
        when(mapper.toEntity(requester, "please", PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER))
                .thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        // Act
        CustomResponseDTO res = service.requestVerifiedPromotion(requester, dto);

        // Assert
        assertThat(res.getMessage()).isEqualTo("Promotion request submitted");
        verify(repository).save(entity);
    }

    // ---------- listPendingVerifiedRequests ----------

    @Test
    void listPendingVerifiedRequests_mapsEachEntityToDto() {
        // Arrange
        PromotionRequest r1 = new PromotionRequest();
        PromotionRequest r2 = new PromotionRequest();

        PromotionRequestResponseDTO d1 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);
        PromotionRequestResponseDTO d2 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);

        when(repository.findAllByStatusAndRequestedRole(PromotionRequestStatus.PENDING, Role.VERIFIED_MEMBER))
                .thenReturn(List.of(r1, r2));
        when(mapper.toDto(r1)).thenReturn(d1);
        when(mapper.toDto(r2)).thenReturn(d2);

        // Act
        List<PromotionRequestResponseDTO> out = service.listPendingVerifiedRequests();

        // Assert
        assertThat(out).containsExactly(d1, d2);
    }

    // ---------- approveVerifiedPromotion ----------

    @Test
    void approveVerifiedPromotion_throws_ifRequestNotFound() {
        when(repository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.approveVerifiedPromotion(mock(User.class), 123L));
    }

    @Test
    void approveVerifiedPromotion_throws_ifNotPending() {
        PromotionRequest req = new PromotionRequest();
        ReflectionTestUtils.setField(req, "status", PromotionRequestStatus.APPROVED);
        ReflectionTestUtils.setField(req, "requestedRole", Role.VERIFIED_MEMBER);
        when(repository.findById(1L)).thenReturn(Optional.of(req));

        assertThrows(EntityUpdateException.class, () -> service.approveVerifiedPromotion(mock(User.class), 1L));
    }

    @Test
    void approveVerifiedPromotion_throws_ifWrongRequestType() {
        PromotionRequest req = new PromotionRequest();
        ReflectionTestUtils.setField(req, "status", PromotionRequestStatus.PENDING);
        ReflectionTestUtils.setField(req, "requestedRole", Role.MODERATOR);
        when(repository.findById(1L)).thenReturn(Optional.of(req));

        assertThrows(EntityUpdateException.class, () -> service.approveVerifiedPromotion(mock(User.class), 1L));
    }

    @Test
    void approveVerifiedPromotion_updatesRequest_andPromotesMember() {
        // Arrange: a pending VERIFIED_MEMBER request
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.MEMBER);

        PromotionRequest req = new PromotionRequest();
        ReflectionTestUtils.setField(req, "requester", requester);
        ReflectionTestUtils.setField(req, "status", PromotionRequestStatus.PENDING);
        ReflectionTestUtils.setField(req, "requestedRole", Role.VERIFIED_MEMBER);

        when(repository.findById(5L)).thenReturn(Optional.of(req));

        // Mock fluent builders returned by mappers
        // PromotionRequest builder
        var prBuilder = mock(click.reelscout.backend.builder.definition.PromotionRequestBuilder.class, Answers.RETURNS_SELF);
        PromotionRequest updatedReq = new PromotionRequest();
        when(mapper.toBuilder(req)).thenReturn(prBuilder);
        when(prBuilder.build()).thenReturn(updatedReq);

        // Member builder
        var memberBuilder = mock(click.reelscout.backend.builder.definition.MemberBuilder.class, Answers.RETURNS_SELF);
        Member updatedMember = new Member();
        when(memberMapper.toBuilder(requester)).thenReturn(memberBuilder);
        when(memberBuilder.build()).thenReturn(updatedMember);

        when(repository.save(updatedReq)).thenReturn(updatedReq);
        when(userRepository.save(updatedMember)).thenReturn(updatedMember);

        // Act
        CustomResponseDTO out = service.approveVerifiedPromotion(mock(User.class), 5L);

        // Assert
        assertThat(out.getMessage()).isEqualTo("Promotion request approved");
        // Verify fluent calls happened with expected values
        verify(prBuilder).status(PromotionRequestStatus.APPROVED);
        verify(prBuilder).processedBy(any(User.class));
        verify(prBuilder).decisionReason(null);
        verify(memberBuilder).role(Role.VERIFIED_MEMBER);
        verify(repository).save(updatedReq);
        verify(userRepository).save(updatedMember);
    }

    // ---------- rejectVerifiedPromotion ----------

    @Test
    void rejectVerifiedPromotion_throws_ifNotPendingOrWrongType() {
        PromotionRequest req = new PromotionRequest();
        ReflectionTestUtils.setField(req, "status", PromotionRequestStatus.APPROVED);
        ReflectionTestUtils.setField(req, "requestedRole", Role.VERIFIED_MEMBER);
        when(repository.findById(9L)).thenReturn(Optional.of(req));

        User moderator = mock(User.class);
        PromotionDecisionRequestDTO dto = new PromotionDecisionRequestDTO();
        assertThrows(EntityUpdateException.class, () -> service.rejectVerifiedPromotion(moderator, 9L, dto));
    }

    @Test
    void rejectVerifiedPromotion_updatesRequest_withReason() {
        // Arrange
        PromotionRequest req = new PromotionRequest();
        ReflectionTestUtils.setField(req, "status", PromotionRequestStatus.PENDING);
        ReflectionTestUtils.setField(req, "requestedRole", Role.VERIFIED_MEMBER);
        when(repository.findById(7L)).thenReturn(Optional.of(req));

        PromotionDecisionRequestDTO dto = new PromotionDecisionRequestDTO();
        dto.setReason("insufficient requirements");

        var prBuilder = mock(click.reelscout.backend.builder.definition.PromotionRequestBuilder.class, Answers.RETURNS_SELF);
        PromotionRequest updatedReq = new PromotionRequest();
        when(mapper.toBuilder(req)).thenReturn(prBuilder);
        when(prBuilder.build()).thenReturn(updatedReq);
        when(repository.save(updatedReq)).thenReturn(updatedReq);

        // Act
        CustomResponseDTO out = service.rejectVerifiedPromotion(mock(User.class), 7L, dto);

        // Assert
        assertThat(out.getMessage()).isEqualTo("Promotion request rejected");
        verify(prBuilder).status(PromotionRequestStatus.REJECTED);
        verify(prBuilder).processedBy(any(User.class));
        verify(prBuilder).decisionReason("insufficient requirements");
        verify(repository).save(updatedReq);
    }

    // ---------- requestModeratorPromotion ----------

    @Test
    void requestModeratorPromotion_throws_ifAlreadyModeratorOrHigher() {
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.MODERATOR);

        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        assertThrows(EntityCreateException.class,
                () -> service.requestModeratorPromotion(requester, dto));
    }

    @Test
    void requestModeratorPromotion_throws_ifNotVerified() {
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.MEMBER);

        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        assertThrows(EntityCreateException.class,
                () -> service.requestModeratorPromotion(requester, dto));
    }

    @Test
    void requestModeratorPromotion_throws_ifPendingAlreadyExists() {
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.VERIFIED_MEMBER);

        when(repository.findByRequesterAndStatusAndRequestedRole(
                requester, PromotionRequestStatus.PENDING, Role.MODERATOR)
        ).thenReturn(Optional.of(new PromotionRequest()));

        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        assertThrows(EntityCreateException.class,
                () -> service.requestModeratorPromotion(requester, dto));
    }

    @Test
    void requestModeratorPromotion_saves_andReturnsMessage_onHappyPath() {
        Member requester = new Member();
        ReflectionTestUtils.setField(requester, "role", Role.VERIFIED_MEMBER);

        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        dto.setMessage("mod me");

        PromotionRequest entity = new PromotionRequest();
        when(mapper.toEntity(requester, "mod me", PromotionRequestStatus.PENDING, Role.MODERATOR))
                .thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        CustomResponseDTO res = service.requestModeratorPromotion(requester, dto);

        assertThat(res.getMessage()).isEqualTo("Moderator promotion request submitted");
        verify(repository).save(entity);
    }

    // ---------- listPendingModeratorRequests ----------

    @Test
    void listPendingModeratorRequests_mapsEachEntityToDto() {
        PromotionRequest r1 = new PromotionRequest();
        PromotionRequest r2 = new PromotionRequest();
        PromotionRequestResponseDTO d1 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);
        PromotionRequestResponseDTO d2 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);

        when(repository.findAllByStatusAndRequestedRole(PromotionRequestStatus.PENDING, Role.MODERATOR))
                .thenReturn(List.of(r1, r2));
        when(mapper.toDto(r1)).thenReturn(d1);
        when(mapper.toDto(r2)).thenReturn(d2);

        List<PromotionRequestResponseDTO> out = service.listPendingModeratorRequests();

        assertThat(out).containsExactly(d1, d2);
    }

    // ---------- myRequests ----------

    @Test
    void myRequests_mapsEntitiesToDtos() {
        Member requester = new Member();
        PromotionRequest r1 = new PromotionRequest();
        PromotionRequest r2 = new PromotionRequest();
        PromotionRequestResponseDTO d1 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);
        PromotionRequestResponseDTO d2 = new PromotionRequestResponseDTO(null, null, null, null, null, null, null, null, null);

        when(repository.findAllByRequester(requester)).thenReturn(List.of(r1, r2));
        when(mapper.toDto(r1)).thenReturn(d1);
        when(mapper.toDto(r2)).thenReturn(d2);

        List<PromotionRequestResponseDTO> out = service.myRequests(requester);

        assertThat(out).containsExactly(d1, d2);
    }
}