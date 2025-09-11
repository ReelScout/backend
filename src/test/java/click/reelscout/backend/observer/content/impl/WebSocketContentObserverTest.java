package click.reelscout.backend.observer.content.impl;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.observer.content.ContentSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WebSocketContentObserver}.
 * Uses Mockito to mock dependencies and verify interactions.
 * Tests the register, unregister, and onContentCreated methods.
 */
@ExtendWith(MockitoExtension.class)
class WebSocketContentObserverTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ContentSubject contentSubject;

    @InjectMocks
    private WebSocketContentObserver observer;

    /**
     * Tests that the observer registers itself with the ContentSubject when register() is called.
     * Verifies that the correct method on the subject is invoked.
     */
    @Test
    @DisplayName("register(): registers this observer on the ContentSubject")
    void register_registersObserver() {
        // Act
        observer.register();

        // Assert
        // Verify the observer registers itself with the subject
        verify(contentSubject).registerObserver(observer);
        verifyNoMoreInteractions(contentSubject, messagingTemplate);
    }

    /**
     * Tests that the observer unregisters itself from the ContentSubject when unregister() is called.
     * Verifies that the correct method on the subject is invoked.
     */
    @Test
    @DisplayName("unregister(): removes this observer from the ContentSubject")
    void unregister_removesObserver() {
        // Act
        observer.unregister();

        // Assert
        // Verify the observer unregisters itself from the subject
        verify(contentSubject).removeObserver(observer);
        verifyNoMoreInteractions(contentSubject, messagingTemplate);
    }

    /**
     * Tests that when onContentCreated() is called, a STOMP message is sent to the correct destination
     * with the provided payload. Verifies that no other interactions occur.
     */
    @Test
    @DisplayName("onContentCreated(): sends a STOMP message to /queue/content/new with the payload")
    void onContentCreated_sendsStompMessage() {
        // Arrange
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        // Act
        observer.onContentCreated(payload);

        // Assert
        // Verify the message is sent to the correct destination with the same payload
        verify(messagingTemplate).convertAndSend("/queue/content/new", payload);
        verifyNoMoreInteractions(messagingTemplate);
        verifyNoInteractions(contentSubject);
    }
}