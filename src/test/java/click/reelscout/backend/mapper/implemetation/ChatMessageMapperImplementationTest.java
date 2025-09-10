package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ChatMessageBuilder;
import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.model.jpa.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ChatMessageMapperImplementation.
 * No Spring context: we instantiate the mapper and use Mockito for its collaborators.
 */
class ChatMessageMapperImplementationTest {

    private ChatMessageBuilder builder; // mocked builder with RETURNS_SELF to support chaining
    private ChatMessageMapperImplementation mapper;

    @BeforeEach
    void setUp() {
        builder = mock(ChatMessageBuilder.class, RETURNS_SELF);
        mapper = new ChatMessageMapperImplementation(builder);
    }

    @Test
    void toDto_shouldMapAllFields() {
        // Arrange
        ChatMessage msg = mock(ChatMessage.class);
        String sender = "alice";
        String recipient = "bob";
        String content = "hello";
        LocalDateTime ts = LocalDateTime.now();

        when(msg.getSender()).thenReturn(sender);
        when(msg.getRecipient()).thenReturn(recipient);
        when(msg.getContent()).thenReturn(content);
        when(msg.getTimestamp()).thenReturn(ts);

        // Act
        ChatMessageResponseDTO dto = mapper.toDto(msg);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(sender, dto.getSender(), "Sender should match");
        assertEquals(recipient, dto.getRecipient(), "Recipient should match");
        assertEquals(content, dto.getContent(), "Content should match");
        assertEquals(ts, dto.getTimestamp(), "Timestamp should match");
    }

    @Test
    void toBuilder_shouldPopulateBuilderWithMessageValues_andReturnSameInstance() {
        // Arrange
        ChatMessage msg = mock(ChatMessage.class);
        Long id = 123L;
        String sender = "alice";
        String recipient = "bob";
        String content = "hello";
        LocalDateTime ts = LocalDateTime.now();

        when(msg.getId()).thenReturn(id);
        when(msg.getSender()).thenReturn(sender);
        when(msg.getRecipient()).thenReturn(recipient);
        when(msg.getContent()).thenReturn(content);
        when(msg.getTimestamp()).thenReturn(ts);

        // Act
        ChatMessageBuilder returned = mapper.toBuilder(msg);

        // Assert
        // The mapper should return the SAME builder instance that it holds
        assertSame(builder, returned, "Mapper should return the same builder instance");

        // And all builder methods should be called with matching values (chain order documented)
        InOrder inOrder = inOrder(builder);
        inOrder.verify(builder).id(id);
        inOrder.verify(builder).sender(sender);
        inOrder.verify(builder).recipient(recipient);
        inOrder.verify(builder).content(content);
        inOrder.verify(builder).timestamp(ts);
        verifyNoMoreInteractions(builder);
    }

    @Test
    void toEntity_shouldBuildEntityFromRequestAndSender_andForceNullIdAndTimestamp() {
        // Arrange
        ChatMessageRequestDTO req = mock(ChatMessageRequestDTO.class);
        String sender = "carol";
        String recipient = "dave";
        String content = "ping";

        when(req.getRecipient()).thenReturn(recipient);
        when(req.getContent()).thenReturn(content);

        // The entity returned by builder.build()
        ChatMessage built = mock(ChatMessage.class);
        when(builder.build()).thenReturn(built);

        // Act
        ChatMessage entity = mapper.toEntity(req, sender);

        // Assert
        assertSame(built, entity, "Mapper should return the exact instance built by builder");

        // Verify chained calls (id and timestamp explicitly set to null)
        InOrder inOrder = inOrder(builder);
        inOrder.verify(builder).id(null);
        inOrder.verify(builder).sender(sender);
        inOrder.verify(builder).recipient(recipient);
        inOrder.verify(builder).content(content);
        inOrder.verify(builder).timestamp(null);
        inOrder.verify(builder).build();
        verifyNoMoreInteractions(builder);
    }
}