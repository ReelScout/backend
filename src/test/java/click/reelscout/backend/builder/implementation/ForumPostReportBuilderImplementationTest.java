package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumPostReport;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link ForumPostReportBuilderImplementation}.
 * <p>
 * These tests verify that each fluent setter correctly sets the corresponding field
 * and returns the same builder instance for chaining. The build() method is also tested
 * to ensure it creates a ForumPostReport with all fields copied from the builder.
 * <p>
 * Mockito is used to create mock instances of ForumPost and User to avoid dependencies
 * on their implementations.
 */
class ForumPostReportBuilderImplementationTest {
    /**
     * Tests that the id() method sets the id field and returns the same builder instance.
     */
    @Test
    void id_setsField_andReturnsSameBuilder() {
        // Arrange
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();

        // Act: call the fluent setter
        ForumPostReportBuilderImplementation returned =
                (ForumPostReportBuilderImplementation) builder.id(10L);

        // Assert: field set + fluent API returns the same instance
        assertThat(builder.getId()).isEqualTo(10L);
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Tests that the post() method sets the post field and returns the same builder instance.
     */
    @Test
    void post_setsField_andReturnsSameBuilder() {
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        // Use a mock to avoid relying on ForumPost constructors/behavior
        ForumPost post = mock(ForumPost.class);

        ForumPostReportBuilderImplementation returned =
                (ForumPostReportBuilderImplementation) builder.post(post);

        assertThat(builder.getPost()).isSameAs(post);
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Tests that the reporter() method sets the reporter field and returns the same builder instance.
     */
    @Test
    void reporter_setsField_andReturnsSameBuilder() {
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        // User is abstract -> use a Mockito mock
        User reporter = mock(User.class);

        ForumPostReportBuilderImplementation returned =
                (ForumPostReportBuilderImplementation) builder.reporter(reporter);

        assertThat(builder.getReporter()).isSameAs(reporter);
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Tests that the reason() method sets the reason field and returns the same builder instance.
     */
    @Test
    void reason_setsField_andReturnsSameBuilder() {
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();

        ForumPostReportBuilderImplementation returned =
                (ForumPostReportBuilderImplementation) builder.reason("spam");

        assertThat(builder.getReason()).isEqualTo("spam");
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Tests that the createdAt() method sets the createdAt field and returns the same builder instance.
     */
    @Test
    void createdAt_setsField_andReturnsSameBuilder() {
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        LocalDateTime now = LocalDateTime.now();

        ForumPostReportBuilderImplementation returned =
                (ForumPostReportBuilderImplementation) builder.createdAt(now);

        assertThat(builder.getCreatedAt()).isEqualTo(now);
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Tests that the build() method creates a ForumPostReport with all fields copied from the builder.
     */
    @Test
    void build_returnsReport_withAllValuesCopied() {
        // Arrange: prepare sample data
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        Long id = 99L;
        ForumPost post = mock(ForumPost.class);
        User reporter = mock(User.class);
        String reason = "abusive language";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);

        // Act: chain fluent API and build
        ForumPostReport report = builder
                .id(id)
                .post(post)
                .reporter(reporter)
                .reason(reason)
                .createdAt(createdAt)
                .build();

        // Assert: object created
        assertThat(report).isNotNull();

        // Assert: entity fields match builder values (via reflection to avoid relying on public API)
        assertThat(ReflectionTestUtils.getField(report, "id")).isEqualTo(id);
        assertThat(ReflectionTestUtils.getField(report, "post")).isSameAs(post);
        assertThat(ReflectionTestUtils.getField(report, "reporter")).isSameAs(reporter);
        assertThat(ReflectionTestUtils.getField(report, "reason")).isEqualTo(reason);
        assertThat(ReflectionTestUtils.getField(report, "createdAt")).isEqualTo(createdAt);
    }

    /**
     * Tests that the build() method can handle null optional fields without throwing exceptions.
     */
    @Test
    void build_allowsNullOptionalFields() {
        // Arrange: leave reason/createdAt as null
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        ForumPost post = mock(ForumPost.class);
        User reporter = mock(User.class);

        // Act
        ForumPostReport report = builder
                .id(1L)
                .post(post)
                .reporter(reporter)
                .build();

        // Assert: object created and nulls preserved
        assertThat(report).isNotNull();
        assertThat(ReflectionTestUtils.getField(report, "reason")).isNull();
        assertThat(ReflectionTestUtils.getField(report, "createdAt")).isNull();
    }

    /**
     * Tests that multiple fluent setter calls can be chained together in a single expression.
     */
    @Test
    void fluentApi_supportsChaining() {
        // Arrange
        ForumPostReportBuilderImplementation builder = new ForumPostReportBuilderImplementation();
        ForumPost post = mock(ForumPost.class);
        User reporter = mock(User.class);

        // Act: chain multiple calls in a single expression
        ForumPostReportBuilderImplementation returned = (ForumPostReportBuilderImplementation) builder
                .id(7L)
                .post(post)
                .reporter(reporter)
                .reason("duplicate content");

        // Assert: same builder instance and fields set
        assertThat(returned).isSameAs(builder);
        assertThat(builder.getId()).isEqualTo(7L);
        assertThat(builder.getPost()).isSameAs(post);
        assertThat(builder.getReporter()).isSameAs(reporter);
        assertThat(builder.getReason()).isEqualTo("duplicate content");
    }
}