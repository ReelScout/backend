package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.PromotionRequest;
import click.reelscout.backend.model.jpa.PromotionRequestStatus;
import click.reelscout.backend.model.jpa.Role;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link PromotionRequestBuilderImplementation}.
 * Tests fluent setters and build() method.
 */
class PromotionRequestBuilderImplementationTest {

    /**
     * Tests that the fluent setters set the fields correctly and return the same builder instance.
     */
    @Test
    void fluentSetters_setFields_andReturnSameBuilder() {
        // Arrange
        PromotionRequestBuilderImplementation builder = new PromotionRequestBuilderImplementation();

        // Prepare sample values
        Long id = 11L;
        Member requester = new Member();
        PromotionRequestStatus status = PromotionRequestStatus.PENDING;
        Role requestedRole = Role.VERIFIED_MEMBER;
        String message = "please verify me";
        String decisionReason = "ok";
        User processedBy = mock(User.class);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act: chain fluent API
        PromotionRequestBuilderImplementation returned = (PromotionRequestBuilderImplementation) builder
                .id(id)
                .requester(requester)
                .status(status)
                .requestedRole(requestedRole)
                .message(message)
                .decisionReason(decisionReason)
                .processedBy(processedBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt);

        // Assert: fields set and same instance returned
        assertThat(returned).isSameAs(builder);
        assertThat(builder.getId()).isEqualTo(id);
        assertThat(builder.getRequester()).isSameAs(requester);
        assertThat(builder.getStatus()).isEqualTo(status);
        assertThat(builder.getRequestedRole()).isEqualTo(requestedRole);
        assertThat(builder.getMessage()).isEqualTo(message);
        assertThat(builder.getDecisionReason()).isEqualTo(decisionReason);
        assertThat(builder.getProcessedBy()).isSameAs(processedBy);
        assertThat(builder.getCreatedAt()).isEqualTo(createdAt);
        assertThat(builder.getUpdatedAt()).isEqualTo(updatedAt);
    }

    /**
     * Tests that build() creates a PromotionRequest with all fields copied from the builder.
     */
    @Test
    void build_returnsPromotionRequest_withAllValuesCopied() {
        // Arrange
        PromotionRequestBuilderImplementation builder = new PromotionRequestBuilderImplementation();

        Long id = 99L;
        Member requester = new Member();
        PromotionRequestStatus status = PromotionRequestStatus.APPROVED;
        Role requestedRole = Role.MODERATOR;
        String message = "mod me";
        String decisionReason = "meets criteria";
        User processedBy = mock(User.class);
        LocalDateTime createdAt = LocalDateTime.now().minusHours(3);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

        // Act
        PromotionRequest pr = builder
                .id(id)
                .requester(requester)
                .status(status)
                .requestedRole(requestedRole)
                .message(message)
                .decisionReason(decisionReason)
                .processedBy(processedBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert: object created
        assertThat(pr).isNotNull();

        // Assert: entity fields match builder values (via reflection to avoid relying on public API)
        assertThat(ReflectionTestUtils.getField(pr, "id")).isEqualTo(id);
        assertThat(ReflectionTestUtils.getField(pr, "requester")).isSameAs(requester);
        assertThat(ReflectionTestUtils.getField(pr, "status")).isEqualTo(status);
        assertThat(ReflectionTestUtils.getField(pr, "requestedRole")).isEqualTo(requestedRole);
        assertThat(ReflectionTestUtils.getField(pr, "message")).isEqualTo(message);
        assertThat(ReflectionTestUtils.getField(pr, "decisionReason")).isEqualTo(decisionReason);
        assertThat(ReflectionTestUtils.getField(pr, "processedBy")).isSameAs(processedBy);
        assertThat(ReflectionTestUtils.getField(pr, "createdAt")).isEqualTo(createdAt);
        assertThat(ReflectionTestUtils.getField(pr, "updatedAt")).isEqualTo(updatedAt);
    }

    /**
     * Tests that build() allows optional fields to be null and preserves null values.
     */
    @Test
    void build_allowsNullOptionalFields() {
        // Arrange: leave decisionReason/processedBy/timestamps as null
        PromotionRequestBuilderImplementation builder = new PromotionRequestBuilderImplementation();
        Member requester = new Member();

        // Act
        PromotionRequest pr = builder
                .id(1L)
                .requester(requester)
                .status(PromotionRequestStatus.PENDING)
                .requestedRole(Role.VERIFIED_MEMBER)
                .message("hi")
                .build();

        // Assert: object created and nulls preserved for optional fields
        assertThat(pr).isNotNull();
        assertThat(ReflectionTestUtils.getField(pr, "decisionReason")).isNull();
        assertThat(ReflectionTestUtils.getField(pr, "processedBy")).isNull();
        assertThat(ReflectionTestUtils.getField(pr, "createdAt")).isNull();
        assertThat(ReflectionTestUtils.getField(pr, "updatedAt")).isNull();
    }
}