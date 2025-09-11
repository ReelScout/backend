package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.mapper.definition.ChatMessageMapper;
import click.reelscout.backend.model.jpa.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageMapperImplementation implements ChatMessageMapper {

    private final ChatMessageBuilder chatMessageBuilder;

    /** {@inheritDoc} */
    @Override
    public ChatMessageResponseDTO toDto(ChatMessage message) {
        return new ChatMessageResponseDTO(
                message.getSender(),
                message.getRecipient(),
                message.getContent(),
                message.getTimestamp()
        );
    }

    /** {@inheritDoc} */
    @Override
    public ChatMessageBuilder toBuilder(ChatMessage message) {
        return chatMessageBuilder
                .id(message.getId())
                .sender(message.getSender())
                .recipient(message.getRecipient())
                .content(message.getContent())
                .timestamp(message.getTimestamp());
    }

    /** {@inheritDoc} */
    @Override
    public ChatMessage toEntity(ChatMessageRequestDTO request, String sender) {
        return chatMessageBuilder
                .id(null)
                .sender(sender)
                .recipient(request.getRecipient())
                .content(request.getContent())
                .timestamp(null)
                .build();
    }
}
