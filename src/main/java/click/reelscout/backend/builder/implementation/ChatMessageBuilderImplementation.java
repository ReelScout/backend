package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import click.reelscout.backend.model.jpa.ChatMessage;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
public class ChatMessageBuilderImplementation implements ChatMessageBuilder {
    private Long id;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;

    @Override
    public ChatMessageBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ChatMessageBuilder sender(String sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public ChatMessageBuilder recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    @Override
    public ChatMessageBuilder content(String content) {
        this.content = content;
        return this;
    }

    @Override
    public ChatMessageBuilder timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public ChatMessage build() {
        return new ChatMessage(this);
    }
}
