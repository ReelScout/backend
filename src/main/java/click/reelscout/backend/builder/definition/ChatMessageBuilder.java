package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.ChatMessage;

import java.time.LocalDateTime;

public interface ChatMessageBuilder extends EntityBuilder<ChatMessage, ChatMessageBuilder> {
    ChatMessageBuilder sender(String sender);
    ChatMessageBuilder recipient(String recipient);
    ChatMessageBuilder content(String content);
    ChatMessageBuilder timestamp(LocalDateTime timestamp);
}
