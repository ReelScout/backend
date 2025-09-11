package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.PromotionRequestBuilder;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.PromotionRequest;
import click.reelscout.backend.model.jpa.PromotionRequestStatus;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PromotionRequestMapperImplementation}.
 * Uses Mockito to mock dependencies and verify interactions.
 * Covers mapping between entity and DTO, and builder usage.
 */
@ExtendWith(MockitoExtension.class)
class PromotionRequestMapperImplementationTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private PromotionRequestBuilder builder;

    private PromotionRequestMapperImplementation mapper;

    @BeforeEach
    void setUp() {
        mapper = new PromotionRequestMapperImplementation(builder);
    }

    /**
     * Tests that toDto correctly maps all fields from PromotionRequest entity
     * to PromotionRequestResponseDTO.
     */
    @Test
    void toDto_mapsAllFields() {
        // Arrange: a PromotionRequest with all fields populated
        PromotionRequest req = mock(PromotionRequest.class);

        Long id = 7L;
        Member requester = mock(Member.class);
        Long requesterId = 33L;
        String requesterUsername = "alice";
        PromotionRequestStatus status = PromotionRequestStatus.PENDING;
        Role requestedRole = Role.MODERATOR;
        String message = "please promote me";
        String decisionReason = "ok";
        LocalDateTime createdAt = LocalDateTime.parse("2024-05-01T10:15:30");
        LocalDateTime updatedAt = LocalDateTime.parse("2024-05-02T11:16:31");

        when(req.getId()).thenReturn(id);
        when(req.getRequester()).thenReturn(requester);
        when(requester.getId()).thenReturn(requesterId);
        when(requester.getUsername()).thenReturn(requesterUsername);
        when(req.getStatus()).thenReturn(status);
        when(req.getRequestedRole()).thenReturn(requestedRole);
        when(req.getMessage()).thenReturn(message);
        when(req.getDecisionReason()).thenReturn(decisionReason);
        when(req.getCreatedAt()).thenReturn(createdAt);
        when(req.getUpdatedAt()).thenReturn(updatedAt);

        // Act
        PromotionRequestResponseDTO dto = mapper.toDto(req);

        // Assert: all fields should match 1:1
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getRequesterId()).isEqualTo(requesterId);
        assertThat(dto.getRequesterUsername()).isEqualTo(requesterUsername);
        assertThat(dto.getStatus()).isEqualTo(status);
        assertThat(dto.getRequestedRole()).isEqualTo(requestedRole);
        assertThat(dto.getMessage()).isEqualTo(message);
        assertThat(dto.getDecisionReason()).isEqualTo(decisionReason);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    }

    /**
     * Tests that toBuilder correctly forwards all fields from the PromotionRequest entity
     * to the PromotionRequestBuilder for further building or modification.
     */
    @Test
    void toBuilder_copiesAllFields_andReturnsSameBuilder() {
        // Arrange
        PromotionRequest req = mock(PromotionRequest.class);

        Long id = 9L;
        Member requester = mock(Member.class);
        PromotionRequestStatus status = PromotionRequestStatus.APPROVED;
        Role requestedRole = Role.VERIFIED_MEMBER;
        String message = "done";
        String decisionReason = "meets criteria";
        User processedBy = mock(User.class);
        LocalDateTime createdAt = LocalDateTime.parse("2024-01-01T00:00:00");
        LocalDateTime updatedAt = LocalDateTime.parse("2024-01-02T00:00:00");

        when(req.getId()).thenReturn(id);
        when(req.getRequester()).thenReturn(requester);
        when(req.getStatus()).thenReturn(status);
        when(req.getRequestedRole()).thenReturn(requestedRole);
        when(req.getMessage()).thenReturn(message);
        when(req.getDecisionReason()).thenReturn(decisionReason);
        when(req.getProcessedBy()).thenReturn(processedBy);
        when(req.getCreatedAt()).thenReturn(createdAt);
        when(req.getUpdatedAt()).thenReturn(updatedAt);

        // Act
        PromotionRequestBuilder returned = mapper.toBuilder(req);

        // Assert: the mapper must use and return the same builder instance
        assertThat(returned).isSameAs(builder);

        // Verify all chained setter calls with exact values
        verify(builder).id(id);
        verify(builder).requester(requester);
        verify(builder).status(status);
        verify(builder).requestedRole(requestedRole);
        verify(builder).message(message);
        verify(builder).decisionReason(decisionReason);
        verify(builder).processedBy(processedBy);
        verify(builder).createdAt(createdAt);
        verify(builder).updatedAt(updatedAt);

        // No build() should be called in toBuilder
        verify(builder, never()).build();
    }

    /**
     * Tests that toEntity correctly uses the builder to create a new PromotionRequest entity
     * with the provided inputs, setting ID, decisionReason, processedBy, createdAt, and updatedAt to null.
     */
    @Test
    void toEntity_buildsWithInputs_andNullsForIdDecisionProcessedByAndTimestamps() {
        // Arrange: inputs for a new entity
        Member requester = mock(Member.class);
        String message = "please";
        PromotionRequestStatus status = PromotionRequestStatus.PENDING;
        Role requestedRole = Role.VERIFIED_MEMBER;

        PromotionRequest built = mock(PromotionRequest.class);
        when(builder.build()).thenReturn(built);

        // Act
        PromotionRequest result =
                mapper.toEntity(requester, message, status, requestedRole);

        // Assert: the returned entity is exactly the one from builder.build()
        assertThat(result).isSameAs(built);

        // Verify the fluent calls with the expected values
        verify(builder).id(null);
        verify(builder).requester(requester);
        verify(builder).status(status);
        verify(builder).requestedRole(requestedRole);
        verify(builder).message(message);

        // Explicitly set to null for a fresh entity
        verify(builder).decisionReason(null);
        verify(builder).processedBy(null);

        // createdAt/updatedAt must be set to null so persistence can populate them
        ArgumentCaptor<LocalDateTime> createdAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> updatedAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(builder).createdAt(createdAtCaptor.capture());
        verify(builder).updatedAt(updatedAtCaptor.capture());
        assertThat(createdAtCaptor.getValue()).isNull();
        assertThat(updatedAtCaptor.getValue()).isNull();

        // build() must be called once to get the entity
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }
}