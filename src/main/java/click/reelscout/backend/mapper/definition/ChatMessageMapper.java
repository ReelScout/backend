package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;

/**
 * Mapper interface for converting between {@link ChatMessage} entities, builders, and DTOs.
 */
public interface ChatMessageMapper {
    /**
     * Convert a {@link ChatMessage} entity to a {@link ChatMessageResponseDTO}.
     *
     * @param message the chat message entity
     * @return the chat message response DTO
     */
    ChatMessageResponseDTO toDto(ChatMessage message);

    /**
     * Convert a {@link ChatMessage} entity to its builder representation.
     *
     * @param message the chat message entity
     * @return the corresponding builder
     */
    ChatMessageBuilder toBuilder(ChatMessage message);

    /**
     * Create a {@link ChatMessage} entity from the given request DTO and sender.
     *
     * @param request the chat message request DTO
     * @param sender  the username of the sender
     * @return the created chat message entity
     */
    ChatMessage toEntity(ChatMessageRequestDTO request, String sender);
}
