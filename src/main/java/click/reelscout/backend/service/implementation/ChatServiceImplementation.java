package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.repository.jpa.ChatMessageRepository;
import click.reelscout.backend.service.definition.ChatService;
import click.reelscout.backend.mapper.definition.ChatMessageMapper;
import click.reelscout.backend.mapper.definition.ConversationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImplementation implements ChatService {

    private final ChatMessageRepository repository;
    private final ChatMessageMapper chatMessageMapper;
    private final ConversationMapper conversationMapper;

    /** {@inheritDoc} */
    @Override
    public ChatMessageResponseDTO saveDirectMessage(Member sender, ChatMessageRequestDTO chatMessageRequestDTO) {
        ChatMessage message = chatMessageMapper.toEntity(chatMessageRequestDTO, sender.getUsername());
        repository.save(message);
        return chatMessageMapper.toDto(message);
    }

    /** {@inheritDoc} */
    @Override
    public Page<ChatMessageResponseDTO> getDirectHistory(String userA, String userB, Pageable pageable) {
        return repository.findDirectHistory(userA, userB, pageable)
                .map(chatMessageMapper::toDto);
    }

    /** {@inheritDoc} */
    @Override
    public List<ConversationResponseDTO> getRecentDirectConversations(String me, int size) {
        // Fetch a larger window to allow de-dup by conversation; cap size sensibly
        int fetch = Math.clamp(size * 5L, size, 500);

        Page<ChatMessage> page = repository.findRecentDmMessages(me, PageRequest.of(0, fetch));

        Map<String, ConversationResponseDTO> byConversation = new LinkedHashMap<>();
        for (ChatMessage message : page.getContent()) {
            String counterpartUsername = message.getSender().equalsIgnoreCase(me) ? message.getRecipient().toLowerCase() : message.getSender().toLowerCase();

            if (byConversation.size() < size) {
                byConversation.computeIfAbsent(
                        counterpartUsername,
                        k -> conversationMapper.toDmConversation(message, k)
                );
            }
        }
        return new ArrayList<>(byConversation.values());
    }
}
