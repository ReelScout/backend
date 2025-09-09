package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;

public interface ChatMessageMapper {
    ChatMessageResponseDTO toDto(ChatMessage message);
    ChatMessageBuilder toBuilder(ChatMessage message);
    ChatMessage toEntity(ChatMessageRequestDTO request, String sender);
}
