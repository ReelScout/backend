package click.reelscout.backend.observer.content;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for ContentEventPublisher (Subject/Publisher).
 */
@ExtendWith(MockitoExtension.class)
class ContentEventPublisherTest {

    @Test
    @DisplayName("registerObserver(): registering a non-null observer makes it receive events")
    void registerObserver_registersAndReceivesEvents() {
        // Arrange
        ContentEventPublisher publisher = new ContentEventPublisher();
        ContentObserver observer = mock(ContentObserver.class);
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        // Act
        publisher.registerObserver(observer);
        publisher.notifyContentCreated(payload);

        // Assert
        // After registration, observer must receive the event
        verify(observer).onContentCreated(payload);
        verifyNoMoreInteractions(observer);
    }

    @Test
    @DisplayName("registerObserver(): ignores null without throwing")
    void registerObserver_ignoresNull() {
        // Arrange
        ContentEventPublisher publisher = new ContentEventPublisher();
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        // Act & Assert (no exception expected)
        publisher.registerObserver(null);
        publisher.notifyContentCreated(payload);
        // No observers registered -> nothing to verify, just ensure no exception is thrown
        verifyNoInteractions(payload);
    }

    @Test
    @DisplayName("removeObserver(): removing an observer prevents further notifications")
    void removeObserver_stopsReceivingEvents() {
        // Arrange
        ContentEventPublisher publisher = new ContentEventPublisher();
        ContentObserver observer = mock(ContentObserver.class);
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        publisher.registerObserver(observer);
        // Sanity check: first notify reaches the observer
        publisher.notifyContentCreated(payload);
        verify(observer, times(1)).onContentCreated(payload);

        // Act: remove and notify again
        publisher.removeObserver(observer);
        publisher.notifyContentCreated(payload);

        // Assert: no additional interactions after removal
        verifyNoMoreInteractions(observer);
    }

    @Test
    @DisplayName("notifyContentCreated(): continues notifying other observers if one throws")
    void notifyContentCreated_continuesOnException() {
        // Arrange
        ContentEventPublisher publisher = new ContentEventPublisher();
        ContentObserver badObserver = mock(ContentObserver.class);
        ContentObserver goodObserver = mock(ContentObserver.class);
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        // First observer throws when notified
        doThrow(new RuntimeException("boom")).when(badObserver).onContentCreated(payload);

        publisher.registerObserver(badObserver);
        publisher.registerObserver(goodObserver);

        // Act
        publisher.notifyContentCreated(payload);

        // Assert
        // Even if one observer fails, others must still be notified
        verify(badObserver).onContentCreated(payload);
        verify(goodObserver).onContentCreated(payload);
        verifyNoMoreInteractions(badObserver, goodObserver);
    }

    @Test
    @DisplayName("notifyContentCreated(): no observers -> no interactions")
    void notifyContentCreated_noObservers_noOps() {
        // Arrange
        ContentEventPublisher publisher = new ContentEventPublisher();
        ContentResponseDTO payload = mock(ContentResponseDTO.class);

        // Act & Assert: nothing should happen, just ensure no exception is thrown
        publisher.notifyContentCreated(payload);

        verifyNoInteractions(payload);
    }
}