package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.ChatMessage;
import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ChatMessageBuilderImplementation}.
 * <p>
 * These tests verify the builder's behavior in isolation, ensuring it correctly
 * manages its internal state and produces {@link ChatMessage} instances as expected.
 * <p>
 * Note: This test assumes that {@link ChatMessage} has standard getters for its properties.
 * If {@link ChatMessage} has validation logic (e.g., non-null constraints), additional tests
 * should be added to cover those scenarios.
 */
class ChatMessageBuilderImplementationTest {
    /**
     * Tests that the default state of the builder has all fields set to null.
     */
    @Test
    @DisplayName("Default state: all fields are null before any setter is called")
    void defaultState_allFieldsNull() {
        // Arrange
        ChatMessageBuilderImplementation builder = new ChatMessageBuilderImplementation();

        // Assert
        // These getters come from Lombok's @Getter on the builder
        assertThat(builder.getId()).as("id").isNull();
        assertThat(builder.getSender()).as("sender").isNull();
        assertThat(builder.getRecipient()).as("recipient").isNull();
        assertThat(builder.getContent()).as("content").isNull();
        assertThat(builder.getTimestamp()).as("timestamp").isNull();
    }

    /**
     * Tests that each setter in the builder returns the same builder instance, allowing method chaining.
     */
    @Test
    @DisplayName("Fluent setters: each setter returns the same builder instance (chainable)")
    void fluentSetters_areChainable() {
        // Arrange
        ChatMessageBuilderImplementation builder = new ChatMessageBuilderImplementation();
        LocalDateTime ts = LocalDateTime.now();

        // Act
        ChatMessageBuilder returned =
                builder
                        .id(1L)
                        .sender("alice")
                        .recipient("bob")
                        .content("hello")
                        .timestamp(ts);

        // Assert
        // Chainability: every call returns the same object instance
        assertThat(returned).isSameAs(builder);

        // Also verify the builder's internal state reflects the inputs
        assertThat(builder.getId()).isEqualTo(1L);
        assertThat(builder.getSender()).isEqualTo("alice");
        assertThat(builder.getRecipient()).isEqualTo("bob");
        assertThat(builder.getContent()).isEqualTo("hello");
        assertThat(builder.getTimestamp()).isEqualTo(ts);
    }

    /**
     * Tests that the build method produces a ChatMessage with fields copied from the builder.
     */
    @Test
    @DisplayName("Build: produces a ChatMessage with fields copied from the builder")
    void build_producesEntityWithGivenValues() {
        // Arrange
        ChatMessageBuilderImplementation builder = new ChatMessageBuilderImplementation();
        Long id = 42L;
        String sender = "carol";
        String recipient = "dave";
        String content = "Hi Dave!";
        LocalDateTime timestamp = LocalDateTime.of(2025, 9, 10, 12, 34, 56);

        builder.id(id)
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .timestamp(timestamp);

        // Act
        ChatMessage message = builder.build();

        // Assert
        // Assumes ChatMessage exposes standard getters for these properties
        assertThat(message).isNotNull();
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getSender()).isEqualTo(sender);
        assertThat(message.getRecipient()).isEqualTo(recipient);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.getTimestamp()).isEqualTo(timestamp);
    }

    /**
     * Tests that building twice returns distinct instances with equal field values.
     */
    @Test
    @DisplayName("Build twice: returns distinct instances with equal field values")
    void buildTwice_returnsDistinctInstancesWithSameValues() {
        // Arrange
        ChatMessageBuilderImplementation builder = new ChatMessageBuilderImplementation();
        builder.id(7L)
                .sender("eve")
                .recipient("frank")
                .content("ping")
                .timestamp(LocalDateTime.of(2025, 1, 2, 3, 4, 5));

        // Act
        ChatMessage first = builder.build();
        ChatMessage second = builder.build();

        // Assert
        // Two builds should not be the exact same object in memory
        assertThat(first).isNotSameAs(second);

        // But their observable state should match since the builder state didn't change
        assertThat(first.getId()).isEqualTo(second.getId());
        assertThat(first.getSender()).isEqualTo(second.getSender());
        assertThat(first.getRecipient()).isEqualTo(second.getRecipient());
        assertThat(first.getContent()).isEqualTo(second.getContent());
        assertThat(first.getTimestamp()).isEqualTo(second.getTimestamp());
    }

    /**
     * Tests that the builder allows empty content and null timestamp if the entity supports it.
     */
    @Test
    @DisplayName("Allows empty content and null timestamp if the entity supports it")
    void build_allowsEmptyContentAndNullTimestamp() {
        // Arrange
        ChatMessageBuilder builder = new ChatMessageBuilderImplementation()
                .id(99L)
                .sender("gina")
                .recipient("harry")
                .content("")          // empty but not null
                .timestamp(null);     // explicitly null

        // Act
        ChatMessage msg = builder.build();

        // Assert
        // If ChatMessage enforces validation, adjust this test accordingly.
        assertThat(msg.getContent()).isEmpty();
        assertThat(msg.getTimestamp()).isNull();
    }
}