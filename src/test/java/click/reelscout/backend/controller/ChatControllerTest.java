package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.ChatMessageRequestDTO;
import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ChatController (no Spring context).
 * We verify delegation to ChatService and that SimpMessagingTemplate sends to both users.
 */
@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ChatService chatService;

    @Mock
    private Authentication authentication;

    @Mock
    private ChatMessageRequestDTO inbound;

    @Mock
    private ChatMessageResponseDTO saved;

    private ChatController controller;

    @BeforeEach
    void setUp() {
        controller = new ChatController(messagingTemplate, chatService);
    }

    @Test
    void sendDirect_shouldDelegateToService_andSendToRecipientAndSender() {
        // Arrange
        Member principal = new Member(); // Only identity is used; no getters invoked by the controller
        when(authentication.getPrincipal()).thenReturn(principal);

        // The service returns a saved response that has sender/recipient populated
        when(saved.getRecipient()).thenReturn("alice");
        when(saved.getSender()).thenReturn("bob");
        when(chatService.saveDirectMessage(principal, inbound)).thenReturn(saved);

        // Act
        controller.sendDirect(inbound, authentication);

        // Assert
        // 1) Service is called with the Member principal and the inbound DTO
        verify(chatService).saveDirectMessage(principal, inbound);

        // 2) Two messages are sent: one to recipient, one to sender, both to /queue/dm, same payload
        verify(messagingTemplate).convertAndSendToUser("alice", "/queue/dm", saved);
        verify(messagingTemplate).convertAndSendToUser("bob", "/queue/dm", saved);

        // Optionally verify ordering (not strictly required, but documents current behavior)
        InOrder inOrder = inOrder(messagingTemplate);
        inOrder.verify(messagingTemplate).convertAndSendToUser("alice", "/queue/dm", saved);
        inOrder.verify(messagingTemplate).convertAndSendToUser("bob", "/queue/dm", saved);

        verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void sendDirect_whenServiceThrows_shouldPropagate_andNotSendMessages() {
        // Arrange
        Member principal = new Member();
        when(authentication.getPrincipal()).thenReturn(principal);
        RuntimeException boom = new RuntimeException("boom");
        when(chatService.saveDirectMessage(principal, inbound)).thenThrow(boom);

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.sendDirect(inbound, authentication),
                "Controller should propagate exceptions from the service");
        assertSame(boom, ex, "The thrown exception should be the same instance");

        // No messages should be sent if saving fails
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void messageMappingAnnotation_shouldBePresent_withDmDestination() throws NoSuchMethodException {
        // Arrange
        Method method = ChatController.class.getDeclaredMethod(
                "sendDirect", ChatMessageRequestDTO.class, org.springframework.security.core.Authentication.class);

        // Act
        MessageMapping mapping = method.getAnnotation(MessageMapping.class);

        // Assert
        assertNotNull(mapping, "@MessageMapping should be present on sendDirect method");
        // It's acceptable for Spring to support either value() or the alias; here we check 'value'
        String[] destinations = mapping.value();
        assertNotNull(destinations, "MessageMapping value should not be null");
        assertTrue(Arrays.asList(destinations).contains("/dm"),
                "MessageMapping should include \"/dm\" destination");
    }

    @Test
    void sendDirect_whenSenderEqualsRecipient_shouldStillSendTwice() {
        // Arrange
        Member principal = new Member();
        when(authentication.getPrincipal()).thenReturn(principal);

        when(saved.getRecipient()).thenReturn("sameUser");
        when(saved.getSender()).thenReturn("sameUser");
        when(chatService.saveDirectMessage(principal, inbound)).thenReturn(saved);

        // Act
        controller.sendDirect(inbound, authentication);

        // Assert
        // Even if sender==recipient, current implementation issues two sends
        verify(messagingTemplate, times(2))
                .convertAndSendToUser("sameUser", "/queue/dm", saved);
        verifyNoMoreInteractions(messagingTemplate);
    }
}