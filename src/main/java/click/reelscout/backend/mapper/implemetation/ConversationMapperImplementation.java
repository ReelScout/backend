package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.mapper.definition.ConversationMapper;
import click.reelscout.backend.model.jpa.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapperImplementation implements ConversationMapper {
    @Override
    public ConversationResponseDTO toDmConversation(ChatMessage lastMessage, String counterpartUsername) {
        return new ConversationResponseDTO(
                counterpartUsername,
                lastMessage.getSender(),
                lastMessage.getContent(),
                lastMessage.getTimestamp()
        );
    }
}
