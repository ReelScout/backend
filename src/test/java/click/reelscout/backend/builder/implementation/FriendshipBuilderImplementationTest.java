package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FriendshipBuilderImplementation.
 * These tests verify:
 *  - The fluent API returns the builder itself (method chaining).
 *  - Each setter stores the provided value inside the builder.
 *  - build() produces a Friendship mirroring the builder's state.
 *  - Last write wins when a setter is called multiple times.
 *  - Null values are accepted and propagated to the built entity.
 */
class FriendshipBuilderImplementationTest {

    @Test
    void fluentApi_shouldReturnSameBuilder_forMethodChaining() {
        // Arrange
        FriendshipBuilderImplementation builder = new FriendshipBuilderImplementation();
        Member requester = new Member();
        Member addressee = new Member();
        LocalDateTime now = LocalDateTime.now();

        // Act & Assert
        // Each call should return the same builder instance to allow chaining
        assertSame(builder, builder.id(1L), "id() should return the same builder instance");
        assertSame(builder, builder.requester(requester), "requester() should return the same builder instance");
        assertSame(builder, builder.addressee(addressee), "addressee() should return the same builder instance");
        assertSame(builder, builder.status(anyEnumValue()), "status() should return the same builder instance");
        assertSame(builder, builder.createdAt(now), "createdAt() should return the same builder instance");
        assertSame(builder, builder.updatedAt(now), "updatedAt() should return the same builder instance");
    }

    @Test
    void setters_shouldStoreValuesInsideBuilder() {
        // Arrange
        FriendshipBuilderImplementation builder = new FriendshipBuilderImplementation();
        Long id = 42L;
        Member requester = new Member();
        Member addressee = new Member();
        FriendshipStatus status = anyEnumValue();
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        // Act
        builder.id(id)
                .requester(requester)
                .addressee(addressee)
                .status(status)
                .createdAt(created)
                .updatedAt(updated);

        // Assert
        // Builder is annotated with @Getter, so we can directly read back its internal state
        assertEquals(id, builder.getId(), "Builder should keep the provided id");
        assertSame(requester, builder.getRequester(), "Builder should keep the provided requester");
        assertSame(addressee, builder.getAddressee(), "Builder should keep the provided addressee");
        assertEquals(status, builder.getStatus(), "Builder should keep the provided status");
        assertEquals(created, builder.getCreatedAt(), "Builder should keep the provided createdAt");
        assertEquals(updated, builder.getUpdatedAt(), "Builder should keep the provided updatedAt");
    }

    @Test
    void build_shouldProduceFriendshipWithSameValues() {
        // Arrange
        FriendshipBuilderImplementation builder = new FriendshipBuilderImplementation();
        Long id = 7L;
        Member requester = new Member();
        Member addressee = new Member();
        FriendshipStatus status = anyEnumValue();
        LocalDateTime created = LocalDateTime.now().minusHours(5);
        LocalDateTime updated = LocalDateTime.now().minusHours(1);

        builder.id(id)
                .requester(requester)
                .addressee(addressee)
                .status(status)
                .createdAt(created)
                .updatedAt(updated);

        // Act
        Friendship friendship = builder.build();

        // Assert
        // We assume Friendship exposes getters reflecting the builder-provided values
        assertNotNull(friendship, "build() should not return null");
        assertEquals(id, friendship.getId(), "Built Friendship should copy id from builder");
        assertSame(requester, friendship.getRequester(), "Built Friendship should copy requester from builder");
        assertSame(addressee, friendship.getAddressee(), "Built Friendship should copy addressee from builder");
        assertEquals(status, friendship.getStatus(), "Built Friendship should copy status from builder");
        assertEquals(created, friendship.getCreatedAt(), "Built Friendship should copy createdAt from builder");
        assertEquals(updated, friendship.getUpdatedAt(), "Built Friendship should copy updatedAt from builder");
    }

    @Test
    void setters_calledMultipleTimes_shouldKeepLastValue() {
        // Arrange
        FriendshipBuilderImplementation builder = new FriendshipBuilderImplementation();
        Member firstRequester = new Member();
        Member lastRequester = new Member();
        LocalDateTime firstTime = LocalDateTime.now().minusDays(2);
        LocalDateTime lastTime = LocalDateTime.now();

        // Act
        builder.requester(firstRequester)
                .requester(lastRequester) // override
                .createdAt(firstTime)
                .createdAt(lastTime);     // override

        // Assert
        assertSame(lastRequester, builder.getRequester(), "Last requester() call should win");
        assertEquals(lastTime, builder.getCreatedAt(), "Last createdAt() call should win");

        // Also verify in the built object
        Friendship friendship = builder.build();
        assertSame(lastRequester, friendship.getRequester(), "Built Friendship should reflect last requester value");
        assertEquals(lastTime, friendship.getCreatedAt(), "Built Friendship should reflect last createdAt value");
    }

    @Test
    void nullValues_shouldBeAcceptedAndPropagated() {
        // Arrange
        FriendshipBuilderImplementation builder = new FriendshipBuilderImplementation();

        // Act
        builder.id(null)
                .requester(null)
                .addressee(null)
                .status(null)
                .createdAt(null)
                .updatedAt(null);

        Friendship friendship = builder.build();

        // Assert
        assertNull(builder.getId());
        assertNull(builder.getRequester());
        assertNull(builder.getAddressee());
        assertNull(builder.getStatus());
        assertNull(builder.getCreatedAt());
        assertNull(builder.getUpdatedAt());

        assertNull(friendship.getId(), "Built Friendship should allow null id");
        assertNull(friendship.getRequester(), "Built Friendship should allow null requester");
        assertNull(friendship.getAddressee(), "Built Friendship should allow null addressee");
        assertNull(friendship.getStatus(), "Built Friendship should allow null status");
        assertNull(friendship.getCreatedAt(), "Built Friendship should allow null createdAt");
        assertNull(friendship.getUpdatedAt(), "Built Friendship should allow null updatedAt");
    }

    /**
     * Helper to retrieve any valid enum value without depending on concrete names like PENDING/ACCEPTED.
     */
    private static FriendshipStatus anyEnumValue() {
        FriendshipStatus[] values = FriendshipStatus.values();
        assertTrue(values.length > 0, "FriendshipStatus enum should define at least one constant");
        return values[0];
    }
}