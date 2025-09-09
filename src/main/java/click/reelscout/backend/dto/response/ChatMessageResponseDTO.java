package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDTO {
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
}
