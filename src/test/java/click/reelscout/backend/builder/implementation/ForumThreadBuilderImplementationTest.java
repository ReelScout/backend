package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ForumThreadBuilderImplementationTest {

    @Test
    void id_setsField_andReturnsSameBuilder() {
        // Arrange
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();

        // Act
        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.id(123L);

        // Assert: field set and same instance returned (fluent API)
        assertThat(builder.getId()).isEqualTo(123L);
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void content_setsField_andReturnsSameBuilder() {
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        // Use a mock to avoid depending on Content's constructors/behavior
        Content content = mock(Content.class);

        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.content(content);

        assertThat(builder.getContent()).isSameAs(content);
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void title_setsField_andReturnsSameBuilder() {
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();

        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.title("My Thread");

        assertThat(builder.getTitle()).isEqualTo("My Thread");
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void createdBy_setsField_andReturnsSameBuilder() {
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        // User is abstract -> use a Mockito mock
        User creator = mock(User.class);

        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.createdBy(creator);

        assertThat(builder.getCreatedBy()).isSameAs(creator);
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void createdAt_setsField_andReturnsSameBuilder() {
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        LocalDateTime t = LocalDateTime.now().minusDays(1);

        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.createdAt(t);

        assertThat(builder.getCreatedAt()).isEqualTo(t);
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void updatedAt_setsField_andReturnsSameBuilder() {
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        LocalDateTime t = LocalDateTime.now();

        ForumThreadBuilderImplementation returned =
                (ForumThreadBuilderImplementation) builder.updatedAt(t);

        assertThat(builder.getUpdatedAt()).isEqualTo(t);
        assertThat(returned).isSameAs(builder);
    }

    @Test
    void build_returnsForumThread_withAllValuesCopied() {
        // Arrange: prepare sample data
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        Long id = 77L;
        Content content = mock(Content.class);
        User creator = mock(User.class);
        String title = "Thread title";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(3);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

        // Act: chain fluent API and build
        ForumThread thread = builder
                .id(id)
                .content(content)
                .title(title)
                .createdBy(creator)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert: object created
        assertThat(thread).isNotNull();

        // Assert: built entity fields match builder values (via reflection to avoid relying on public API)
        assertThat(ReflectionTestUtils.getField(thread, "id")).isEqualTo(id);
        assertThat(ReflectionTestUtils.getField(thread, "content")).isSameAs(content);
        assertThat(ReflectionTestUtils.getField(thread, "title")).isEqualTo(title);
        assertThat(ReflectionTestUtils.getField(thread, "createdBy")).isSameAs(creator);
        assertThat(ReflectionTestUtils.getField(thread, "createdAt")).isEqualTo(createdAt);
        assertThat(ReflectionTestUtils.getField(thread, "updatedAt")).isEqualTo(updatedAt);
    }

    @Test
    void build_allowsNullOptionalFields() {
        // Arrange: leave timestamps as null
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        Content content = mock(Content.class);
        User creator = mock(User.class);

        // Act
        ForumThread thread = builder
                .id(1L)
                .content(content)
                .title("Null timestamps")
                .createdBy(creator)
                .build();

        // Assert: object created and nulls preserved
        assertThat(thread).isNotNull();
        assertThat(ReflectionTestUtils.getField(thread, "createdAt")).isNull();
        assertThat(ReflectionTestUtils.getField(thread, "updatedAt")).isNull();
    }

    @Test
    void fluentApi_supportsChaining() {
        // Arrange
        ForumThreadBuilderImplementation builder = new ForumThreadBuilderImplementation();
        Content content = mock(Content.class);
        User creator = mock(User.class);

        // Act: chain several calls in a single expression
        ForumThreadBuilderImplementation returned = (ForumThreadBuilderImplementation) builder
                .id(5L)
                .content(content)
                .title("Chained")
                .createdBy(creator);

        // Assert: same builder instance and fields set
        assertThat(returned).isSameAs(builder);
        assertThat(builder.getId()).isEqualTo(5L);
        assertThat(builder.getContent()).isSameAs(content);
        assertThat(builder.getTitle()).isEqualTo("Chained");
        assertThat(builder.getCreatedBy()).isSameAs(creator);
    }
}