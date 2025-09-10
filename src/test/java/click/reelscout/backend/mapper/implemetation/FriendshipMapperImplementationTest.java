package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.FriendshipBuilder;
import click.reelscout.backend.builder.implementation.FriendshipBuilderImplementation;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.dto.response.FriendshipWithUsersResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.Friendship;
import click.reelscout.backend.model.jpa.FriendshipStatus;
import click.reelscout.backend.model.jpa.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FriendshipMapperImplementation}.
 * <p>
 * This version removes direct setters and builds domain objects via builders/constructors
 * so it works with immutable entities.
 */
@ExtendWith(MockitoExtension.class)
class FriendshipMapperImplementationTest {

    // Use RETURNS_SELF to make fluent builder calls work (id(...).requester(...).build(), etc.)
    @Mock(answer = Answers.RETURNS_SELF)
    private FriendshipBuilder friendshipBuilder;

    @InjectMocks
    private FriendshipMapperImplementation mapper;

    private Friendship friendshipEntity;
    private Member requesterEntity;
    private Member addresseeEntity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        requesterEntity = new Member();
        addresseeEntity = new Member();

        createdAt = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        updatedAt = LocalDateTime.of(2024, 6, 7, 8, 9, 10);

        friendshipEntity = new FriendshipBuilderImplementation()
                .id(42L)
                .requester(requesterEntity)
                .addressee(addresseeEntity)
                .status(FriendshipStatus.ACCEPTED)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Test
    void toDto_mapsAllFields_andKeepsProvidedUserDTOs() {
        // Arrange: prepare requester/addressee DTOs (could be records or beans)
        UserResponseDTO requesterDto = mock(UserResponseDTO.class);
        UserResponseDTO addresseeDto = mock(UserResponseDTO.class);

        // Act
        FriendshipResponseDTO dto = mapper.toDto(friendshipEntity, requesterDto, addresseeDto);

        // Assert: type should be FriendshipWithUsersResponseDTO
        assertThat(dto).isInstanceOf(FriendshipWithUsersResponseDTO.class);

        // Assert: verify core fields are propagated
        assertThat(readProp(dto, "id")).isEqualTo(42L);
        assertThat(readProp(dto, "status")).isEqualTo(FriendshipStatus.ACCEPTED);
        assertThat(readProp(dto, "createdAt")).isEqualTo(createdAt);
        assertThat(readProp(dto, "updatedAt")).isEqualTo(updatedAt);

        // Assert: requester/addressee are exactly the same instances we passed in
        assertThat(readProp(dto, "requester")).isSameAs(requesterDto);
        assertThat(readProp(dto, "addressee")).isSameAs(addresseeDto);
    }

    @Test
    void toBuilder_callsBuilderWithEntityFields_andReturnsSameBuilder() {
        // Act
        FriendshipBuilder returned = mapper.toBuilder(friendshipEntity);

        // Assert: the returned builder is the injected one (fluent chain ended)
        assertThat(returned).isSameAs(friendshipBuilder);

        // Verify builder was called with all fields from the entity, in any order
        verify(friendshipBuilder).id(42L);
        verify(friendshipBuilder).requester(requesterEntity);
        verify(friendshipBuilder).addressee(addresseeEntity);
        verify(friendshipBuilder).status(FriendshipStatus.ACCEPTED);
        verify(friendshipBuilder).createdAt(createdAt);
        verify(friendshipBuilder).updatedAt(updatedAt);

        // Ensure no unexpected interactions beyond the expected fluent setters
        verifyNoMoreInteractions(friendshipBuilder);
    }

    @Test
    void toEntity_buildsFriendshipWithGivenInputs_andNullsForIdAndTimestamps() {
        // Arrange: inputs for the entity creation
        Member req = new Member();
        Member addr = new Member();
        FriendshipStatus status = FriendshipStatus.PENDING;

        // Create the entity that the mocked builder will return
        Friendship built = new FriendshipBuilderImplementation()
                .id(null)
                .requester(req)
                .addressee(addr)
                .status(status)
                .createdAt(null)
                .updatedAt(null)
                .build();

        // Stub the final build() call to return our prepared entity
        when(friendshipBuilder.build()).thenReturn(built);

        // Act
        Friendship result = mapper.toEntity(req, addr, status);

        // Assert: the returned entity is exactly the one from builder.build()
        assertThat(result).isSameAs(built);

        // Verify the fluent calls on the builder with the expected values
        verify(friendshipBuilder).id(null);
        verify(friendshipBuilder).requester(req);
        verify(friendshipBuilder).addressee(addr);
        verify(friendshipBuilder).status(status);
        verify(friendshipBuilder).createdAt(null);
        verify(friendshipBuilder).updatedAt(null);
        verify(friendshipBuilder).build();

        verifyNoMoreInteractions(friendshipBuilder);
    }

    // --------------------------------------------------------------------------------------------
    // Small helper that tries both JavaBean "getX()" and Java record-style "x()" accessors.
    // This makes the test robust regardless of how your DTO is implemented.
    // --------------------------------------------------------------------------------------------
    private Object readProp(Object target, String name) {
        try {
            // Try JavaBean getter first: getId(), getStatus(), ...
            Method m = target.getClass().getMethod("get" + capitalize(name));
            return m.invoke(target);
        } catch (NoSuchMethodException ignored) {
            try {
                // Fallback to record-like accessor: id(), status(), ...
                Method m2 = target.getClass().getMethod(name);
                return m2.invoke(target);
            } catch (Exception e) {
                throw new AssertionError(target.getClass().getSimpleName()
                        + " does not expose expected accessors for property '" + name + "'", e);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}