package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;

public interface ConversationMapper {
    ConversationResponseDTO toDmConversation(ChatMessage lastMessage, String counterpartUsername);
}
