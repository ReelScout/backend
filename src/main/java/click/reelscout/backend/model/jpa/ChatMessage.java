package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.ChatMessageBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a chat message between users.
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_sender_recipient_ts", columnList = "sender,recipient,timestamp")
})
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, length = 2000)
    private String content;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ChatMessage(ChatMessageBuilderImplementation b) {
        this.id = b.getId();
        this.sender = b.getSender();
        this.recipient = b.getRecipient();
        this.content = b.getContent();
        this.timestamp = b.getTimestamp();
    }
}
