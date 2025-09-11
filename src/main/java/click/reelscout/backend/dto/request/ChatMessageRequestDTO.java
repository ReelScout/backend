package click.reelscout.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling requests to send a chat message.
 * Contains the content of the message and the recipient's identifier.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDTO {
    private String content;
    private String recipient;
}
