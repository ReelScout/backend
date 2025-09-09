package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationResponseDTO {
    private String counterpartUsername;
    private String lastMessageSender;
    private String lastMessageContent;
    private LocalDateTime lastMessageTimestamp;
}
