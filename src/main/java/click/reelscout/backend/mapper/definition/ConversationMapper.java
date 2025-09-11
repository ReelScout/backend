package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;

/**
 * Mapper interface for converting {@link ChatMessage} entities to conversation DTOs.
 */
public interface ConversationMapper {
    /**
     * Convert the last message in a direct conversation to a {@link ConversationResponseDTO}.
     *
     * @param lastMessage        the last message in the conversation
     * @param counterpartUsername the username of the other participant
     * @return the conversation response DTO
     */
    ConversationResponseDTO toDmConversation(ChatMessage lastMessage, String counterpartUsername);
}
