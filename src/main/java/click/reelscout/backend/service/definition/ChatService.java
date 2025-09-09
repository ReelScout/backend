package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChatService {
    ChatMessageResponseDTO saveDirectMessage(Member sender, ChatMessageRequestDTO chatMessageRequestDTO);

    Page<ChatMessageResponseDTO> getDirectHistory(String userA, String userB, Pageable pageable);

    List<ConversationResponseDTO> getRecentDirectConversations(String me, int size);
}
