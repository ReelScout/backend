package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service definition for direct messaging and conversation retrieval.
 */
public interface ChatService {
    /**
     * Save a direct message sent by a member.
     *
     * @param sender                the member sending the message
     * @param chatMessageRequestDTO message payload
     * @return the saved {@link ChatMessageResponseDTO}
     */
    ChatMessageResponseDTO saveDirectMessage(Member sender, ChatMessageRequestDTO chatMessageRequestDTO);

    /**
     * Retrieve paged direct message history between two users.
     *
     * @param userA    username of one participant
     * @param userB    username of the other participant
     * @param pageable paging parameters
     * @return page of {@link ChatMessageResponseDTO}
     */
    Page<ChatMessageResponseDTO> getDirectHistory(String userA, String userB, Pageable pageable);

    /**
     * Get recent direct conversations for a user.
     *
     * @param me   username of the requesting user
     * @param size desired number of conversations
     * @return list of {@link ConversationResponseDTO}
     */
    List<ConversationResponseDTO> getRecentDirectConversations(String me, int size);
}
