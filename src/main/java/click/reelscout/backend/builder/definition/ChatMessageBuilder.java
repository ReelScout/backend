package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ChatMessage;

import java.time.LocalDateTime;

/**
 * Builder contract for creating {@link ChatMessage} instances.
 */
public interface ChatMessageBuilder extends EntityBuilder<ChatMessage, ChatMessageBuilder> {
    /** Set the sender username. */
    ChatMessageBuilder sender(String sender);
    /** Set the recipient username. */
    ChatMessageBuilder recipient(String recipient);
    /** Set the message content. */
    ChatMessageBuilder content(String content);
    /** Set the message timestamp. */
    ChatMessageBuilder timestamp(LocalDateTime timestamp);
}
