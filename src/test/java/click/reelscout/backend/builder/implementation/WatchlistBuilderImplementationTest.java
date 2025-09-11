package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Watchlist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Pure unit tests for {@link WatchlistBuilderImplementation}.
 * <p>
 * Focus:
 *  - Verify fluent setters return the same builder instance (method chaining)
 *  - Verify the internal state exposed via Lombok @Getter is updated correctly
 *  - Verify null-safety/acceptance for optional fields
 *  - Verify build() returns a non-null Watchlist instance
 * <p>
 * Note:
 *  We intentionally assert on the builder's getters (id, name, contents, isPublic, member)
 *  because the Watchlist entity API is not enforced here. If Watchlist exposes getters
 *  mirroring the builder, you can extend these tests to assert field transfer as well.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistBuilderImplementationTest {

    /**
     * Test that each fluent setter returns the same builder instance to enable method chaining,
     * and that the internal state is updated correctly and exposed via Lombok-generated getters.
     */
    @Test
    @DisplayName("Fluent API should return same instance and set fields")
    void fluentApi_shouldReturnSameInstance_andSetFields() {
        // Arrange
        WatchlistBuilderImplementation builder = new WatchlistBuilderImplementation();
        Long expectedId = 10L;
        String expectedName = "My Watchlist";
        List<Content> expectedContents = List.of(
                mock(Content.class),
                mock(Content.class)
        );
        Boolean expectedIsPublic = Boolean.TRUE;
        Member expectedMember = mock(Member.class);

        // Act
        // Each setter should return the very same builder instance to enable chaining
        WatchlistBuilderImplementation afterId = (WatchlistBuilderImplementation) builder.id(expectedId);
        WatchlistBuilderImplementation afterName = (WatchlistBuilderImplementation) builder.name(expectedName);
        WatchlistBuilderImplementation afterContents = (WatchlistBuilderImplementation) builder.contents(expectedContents);
        WatchlistBuilderImplementation afterIsPublic = (WatchlistBuilderImplementation) builder.isPublic(expectedIsPublic);
        WatchlistBuilderImplementation afterMember = (WatchlistBuilderImplementation) builder.member(expectedMember);

        // Assert
        // 1) Method chaining: every call returns the *same* instance (not a copy)
        assertThat(afterId).isSameAs(builder);
        assertThat(afterName).isSameAs(builder);
        assertThat(afterContents).isSameAs(builder);
        assertThat(afterIsPublic).isSameAs(builder);
        assertThat(afterMember).isSameAs(builder);

        // 2) Internal state is updated and exposed through Lombok-generated getters
        assertThat(builder.getId()).isEqualTo(expectedId);
        assertThat(builder.getName()).isEqualTo(expectedName);
        assertThat(builder.getContents()).containsExactlyElementsOf(expectedContents);
        assertThat(builder.getIsPublic()).isEqualTo(expectedIsPublic);
        assertThat(builder.getMember()).isSameAs(expectedMember);
    }

    /**
     * Test that multiple calls to the same setter overwrite previous values.
     */
    @Test
    @DisplayName("Setter calls should overwrite previous values")
    void setterCalls_shouldOverwritePreviousValues() {
        // Arrange
        WatchlistBuilderImplementation builder = new WatchlistBuilderImplementation();

        // Act
        builder.id(1L).id(2L);
        builder.name("First").name("Second");
        builder.isPublic(Boolean.FALSE).isPublic(Boolean.TRUE);

        List<Content> firstList = new ArrayList<>();
        List<Content> secondList = List.of(mock(Content.class));
        builder.contents(firstList).contents(secondList);

        Member firstMember = mock(Member.class);
        Member secondMember = mock(Member.class);
        builder.member(firstMember).member(secondMember);

        // Assert
        assertThat(builder.getId()).isEqualTo(2L);
        assertThat(builder.getName()).isEqualTo("Second");
        assertThat(builder.getIsPublic()).isTrue();
        assertThat(builder.getContents()).containsExactlyElementsOf(secondList);
        assertThat(builder.getMember()).isSameAs(secondMember);
    }

    /**
     * Test that the builder accepts null values for all optional fields without throwing exceptions.
     */
    @Test
    @DisplayName("Builder should accept null values for optional fields")
    void builder_shouldAcceptNullValues() {
        // Arrange
        WatchlistBuilderImplementation builder = new WatchlistBuilderImplementation();

        // Act
        builder
                .id(null)
                .name(null)
                .contents(null)
                .isPublic(null)
                .member(null);

        // Assert
        assertThat(builder.getId()).isNull();
        assertThat(builder.getName()).isNull();
        assertThat(builder.getContents()).isNull();
        assertThat(builder.getIsPublic()).isNull();
        assertThat(builder.getMember()).isNull();
    }

    /**
     * Test that build() returns a non-null Watchlist instance.
     */
    @Test
    @DisplayName("build() should return a non-null Watchlist instance")
    void build_shouldReturnNonNullWatchlist() {
        // Arrange
        WatchlistBuilderImplementation builder = new WatchlistBuilderImplementation();
        builder.name("Any");

        // Act
        Watchlist watchlist = builder.build();

        // Assert
        assertThat(watchlist).isNotNull();
    }

    /**
     * Smoke test to verify that a full chain of setter calls followed by build() compiles and runs without exceptions.
     */
    @Nested
    @DisplayName("Chaining smoke tests")
    class ChainingSmokeTests {
        @Test
        @DisplayName("Full chain should compile and not throw")
        void fullChain_shouldCompile_andNotThrow() {
            // Arrange & Act
            Watchlist watchlist = new WatchlistBuilderImplementation()
                    .id(99L)
                    .name("Chain")
                    .contents(List.of())
                    .isPublic(false)
                    .member(mock(Member.class))
                    .build();

            // Assert
            assertThat(watchlist).isNotNull();
        }
    }
}