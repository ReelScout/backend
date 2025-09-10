package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.mapper.definition.ChatMessageMapper;
import click.reelscout.backend.mapper.definition.ConversationMapper;
import click.reelscout.backend.model.jpa.ChatMessage;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.repository.jpa.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplementationTest {

    @Mock
    ChatMessageRepository repository;

    @Mock
    ChatMessageMapper chatMessageMapper;

    @Mock
    ConversationMapper conversationMapper;

    @InjectMocks
    ChatServiceImplementation service;

    @Test
    @DisplayName("saveDirectMessage(): maps DTO->entity, saves, then maps entity->DTO")
    void saveDirectMessage_mapsAndSaves() {
        // Arrange
        Member sender = mock(Member.class);
        when(sender.getUsername()).thenReturn("alice");

        ChatMessageRequestDTO req = mock(ChatMessageRequestDTO.class);

        ChatMessage entity = new ChatMessage(); // minimal stub, only used for identity
        ChatMessageResponseDTO dto = mock(ChatMessageResponseDTO.class);

        when(chatMessageMapper.toEntity(req, "alice")).thenReturn(entity);
        when(chatMessageMapper.toDto(entity)).thenReturn(dto);

        // Act
        ChatMessageResponseDTO result = service.saveDirectMessage(sender, req);

        // Assert
        // Verify mapping to entity and save
        verify(chatMessageMapper).toEntity(req, "alice");
        ArgumentCaptor<ChatMessage> savedCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(repository).save(savedCaptor.capture());
        assertThat(savedCaptor.getValue()).isSameAs(entity);

        // Verify mapping to DTO and returned instance
        verify(chatMessageMapper).toDto(entity);
        assertThat(result).isSameAs(dto);

        verifyNoMoreInteractions(repository, chatMessageMapper, conversationMapper);
    }

    @Test
    @DisplayName("getDirectHistory(): repository result is mapped page-wise to DTOs")
    void getDirectHistory_mapsPage() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 20, Sort.by("timestamp").descending());
        ChatMessage m1 = new ChatMessage();
        ChatMessage m2 = new ChatMessage();

        Page<ChatMessage> page = new PageImpl<>(List.of(m1, m2), pageable, 2);
        when(repository.findDirectHistory("alice", "bob", pageable)).thenReturn(page);

        ChatMessageResponseDTO d1 = mock(ChatMessageResponseDTO.class);
        ChatMessageResponseDTO d2 = mock(ChatMessageResponseDTO.class);
        when(chatMessageMapper.toDto(m1)).thenReturn(d1);
        when(chatMessageMapper.toDto(m2)).thenReturn(d2);

        // Act
        Page<ChatMessageResponseDTO> result = service.getDirectHistory("alice", "bob", pageable);

        // Assert
        assertThat(result.getContent()).containsExactly(d1, d2);
        assertThat(result.getPageable()).isEqualTo(pageable);
        assertThat(result.getTotalElements()).isEqualTo(page.getTotalElements());
        assertThat(result.getNumberOfElements()).isEqualTo(page.getNumberOfElements());

        verify(repository).findDirectHistory("alice", "bob", pageable);
        verify(chatMessageMapper).toDto(m1);
        verify(chatMessageMapper).toDto(m2);
        verifyNoMoreInteractions(repository, chatMessageMapper, conversationMapper);
    }

    @Test
    @DisplayName("getRecentDirectConversations(): de-duplicates by counterpart and limits size")
    void getRecentDirectConversations_dedupsAndLimits() {
        // Arrange
        String me = "ME";
        int size = 2;

        // Simulate 4 messages across 3 conversations (counterparts: bob, carol, bob, dave)
        ChatMessage m1 = mkDm("ME", "bob",  LocalDateTime.now().minusMinutes(1)); // me -> bob
        ChatMessage m2 = mkDm("carol", "ME", LocalDateTime.now().minusMinutes(2)); // carol -> me
        ChatMessage m3 = mkDm("ME", "bob",  LocalDateTime.now().minusMinutes(3)); // duplicate conv with bob
        ChatMessage m4 = mkDm("dave", "ME", LocalDateTime.now().minusMinutes(4)); // dave -> me

        // The repository returns a page with these messages in recent-first order
        // Page size used internally is clamped to max 500 and at least 'size'
        // We don't assert the fetch value here; we just return a Page with our test data
        Page<ChatMessage> page = new PageImpl<>(List.of(m1, m2, m3, m4), PageRequest.of(0, 10), 4);
        when(repository.findRecentDmMessages(eq(me), any(PageRequest.class))).thenReturn(page);

        // Define how counterpart is computed and mapping to ConversationResponseDTO
        ConversationResponseDTO convBob   = new ConversationResponseDTO("bob",   "ME",   "hi",  m1.getTimestamp());
        ConversationResponseDTO convCarol = new ConversationResponseDTO("carol", "carol", "yo", m2.getTimestamp());        when(conversationMapper.toDmConversation(m1, "bob")).thenReturn(convBob);
        when(conversationMapper.toDmConversation(m1, "bob")).thenReturn(convBob);
        when(conversationMapper.toDmConversation(m2, "carol")).thenReturn(convCarol);

        // Act
        List<ConversationResponseDTO> recent = service.getRecentDirectConversations(me, size);

        // Assert
        // Expect at most 'size' items, first occurrences win, duplicates skipped
        assertThat(recent).containsExactly(convBob, convCarol);
        // Ensure repository called with page 0 and a clamped fetch between size and 500
        ArgumentCaptor<PageRequest> pr = ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findRecentDmMessages(eq(me), pr.capture());
        assertThat(pr.getValue().getPageNumber()).isZero();
        assertThat(pr.getValue().getPageSize()).isBetween(size, 500);

        // Verify mapper was called only for the first occurrence of a counterpart
        verify(conversationMapper).toDmConversation(m1, "bob");
        verify(conversationMapper).toDmConversation(m2, "carol");
        verifyNoMoreInteractions(conversationMapper);
    }

    @Test
    @DisplayName("getRecentDirectConversations(): counterpart detection works for sent and received messages")
    void getRecentDirectConversations_counterpartDetection() {
        // Arrange
        String me = "me";
        int size = 3;

        ChatMessage sentToZoe     = mkDm("me", "ZOE", LocalDateTime.now());      // counterpart: "zoe"
        ChatMessage receivedFromAnn = mkDm("ANN", "me", LocalDateTime.now());    // counterpart: "ann"

        Page<ChatMessage> page = new PageImpl<>(List.of(sentToZoe, receivedFromAnn));
        when(repository.findRecentDmMessages(eq(me), any(PageRequest.class))).thenReturn(page);

        ConversationResponseDTO convZoe = new ConversationResponseDTO("zoe", "me",  "a", sentToZoe.getTimestamp());
        ConversationResponseDTO convAnn = new ConversationResponseDTO("ann", "ann", "b", receivedFromAnn.getTimestamp());

        when(conversationMapper.toDmConversation(sentToZoe, "zoe")).thenReturn(convZoe);
        when(conversationMapper.toDmConversation(receivedFromAnn, "ann")).thenReturn(convAnn);

        // Act
        List<ConversationResponseDTO> recent = service.getRecentDirectConversations(me, size);

        // Assert
        assertThat(recent).containsExactly(convZoe, convAnn);
        verify(conversationMapper).toDmConversation(sentToZoe, "zoe");
        verify(conversationMapper).toDmConversation(receivedFromAnn, "ann");
        verifyNoMoreInteractions(conversationMapper);
    }

    // ---------- helpers ----------

    private static ChatMessage mkDm(String sender, String recipient, LocalDateTime ts) {
        ChatMessage m = new ChatMessage();
        try {
            var setSender = ChatMessage.class.getMethod("setSender", String.class);
            var setRecipient = ChatMessage.class.getMethod("setRecipient", String.class);
            var setTimestamp = ChatMessage.class.getMethod("setTimestamp", LocalDateTime.class);
            setSender.invoke(m, sender);
            setRecipient.invoke(m, recipient);
            setTimestamp.invoke(m, ts);
        } catch (Exception ignored) {
            // If your entity uses a builder or constructor, replace reflection with that API
        }
        return m;
    }
}