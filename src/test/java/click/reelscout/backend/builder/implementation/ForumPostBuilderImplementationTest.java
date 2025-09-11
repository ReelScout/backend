package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for ForumPostBuilderImplementation.
 * Tests each fluent setter and the build() method.
 */
class ForumPostBuilderImplementationTest {
    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void id_setsField_andReturnsSameBuilder() {
        // Arrange
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();

        // Act: call the fluent setter
        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.id(42L);

        // Assert: field set and same instance returned (fluent API)
        assertThat(builder.getId()).isEqualTo(42L);
        assertThat(returned).isSameAs(builder);
    }

    /**
     * Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void thread_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        ForumThread thread = new ForumThread();

        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.thread(thread);

        assertThat(builder.getThread()).isSameAs(thread);
        assertThat(returned).isSameAs(builder);
    }

    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void author_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        User author = mock(User.class);

        ForumPostBuilderImplementation returned =
                (ForumPostBuilderImplementation) builder.author(author);

        assertThat(builder.getAuthor()).isSameAs(author);
        assertThat(returned).isSameAs(builder);
    }

    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void parent_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        ForumPost parent = new ForumPost(); // assuming default ctor exists

        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.parent(parent);

        assertThat(builder.getParent()).isSameAs(parent);
        assertThat(returned).isSameAs(builder);
    }

    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void body_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();

        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.body("hello world");

        assertThat(builder.getBody()).isEqualTo("hello world");
        assertThat(returned).isSameAs(builder);
    }

    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void createdAt_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        LocalDateTime t = LocalDateTime.now();

        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.createdAt(t);

        assertThat(builder.getCreatedAt()).isEqualTo(t);
        assertThat(returned).isSameAs(builder);
    }

    /** Test that each fluent setter sets the correct field and returns the same builder instance.
     * Also test that build() creates a ForumPost with all fields copied over.
     */
    @Test
    void updatedAt_setsField_andReturnsSameBuilder() {
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        LocalDateTime t = LocalDateTime.now();

        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder.updatedAt(t);

        assertThat(builder.getUpdatedAt()).isEqualTo(t);
        assertThat(returned).isSameAs(builder);
    }

    /** Test that build() creates a ForumPost with all fields copied over from the builder. */
    @Test
    void build_returnsForumPost_withAllValuesCopied() {
        // Arrange: prepare sample data
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        Long id = 99L;
        ForumThread thread = new ForumThread();
        User author = mock(User.class);
        ForumPost parent = new ForumPost();
        String body = "This is the body";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act: chain fluent API and build
        ForumPost post = builder
                .id(id)
                .thread(thread)
                .author(author)
                .parent(parent)
                .body(body)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert: basic sanity check
        assertThat(post).isNotNull();

        // Assert: fields on the built entity match the builder values.
        // We use ReflectionTestUtils to avoid relying on the entity's public API.
        assertThat(ReflectionTestUtils.getField(post, "id")).isEqualTo(id);
        assertThat(ReflectionTestUtils.getField(post, "thread")).isSameAs(thread);
        assertThat(ReflectionTestUtils.getField(post, "author")).isSameAs(author);
        assertThat(ReflectionTestUtils.getField(post, "parent")).isSameAs(parent);
        assertThat(ReflectionTestUtils.getField(post, "body")).isEqualTo(body);
        assertThat(ReflectionTestUtils.getField(post, "createdAt")).isEqualTo(createdAt);
        assertThat(ReflectionTestUtils.getField(post, "updatedAt")).isEqualTo(updatedAt);
    }

    /** Test that build() works even if optional fields are left null in the builder. */
    @Test
    void build_allowsNullOptionalFields() {
        // Arrange: leave parent and timestamps as null
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        ForumThread thread = new ForumThread();
        User author = mock(User.class);

        // Act
        ForumPost post = builder
                .id(1L)
                .thread(thread)
                .author(author)
                .body("no parent & timestamps")
                .build();

        // Assert: object is created and the nulls are preserved
        assertThat(post).isNotNull();
        assertThat(ReflectionTestUtils.getField(post, "parent")).isNull();
        assertThat(ReflectionTestUtils.getField(post, "createdAt")).isNull();
        assertThat(ReflectionTestUtils.getField(post, "updatedAt")).isNull();
    }

    /** Test that multiple fluent setters can be chained in a single expression. */
    @Test
    void fluentApi_supportsChaining() {
        // Arrange
        ForumPostBuilderImplementation builder = new ForumPostBuilderImplementation();
        ForumThread thread = new ForumThread();
        User author = mock(User.class);

        // Act: single chained expression
        ForumPostBuilderImplementation returned = (ForumPostBuilderImplementation) builder
                .id(7L)
                .thread(thread)
                .author(author)
                .body("chained");

        // Assert: still the same builder instance and fields set
        assertThat(returned).isSameAs(builder);
        assertThat(builder.getId()).isEqualTo(7L);
        assertThat(builder.getThread()).isSameAs(thread);
        assertThat(builder.getAuthor()).isSameAs(author);
        assertThat(builder.getBody()).isEqualTo("chained");
    }
}