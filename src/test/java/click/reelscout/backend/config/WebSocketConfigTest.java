package click.reelscout.backend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketConfig.
 * These tests verify broker configuration and STOMP endpoint registration (with and without SockJS).
 */
@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration wsRegistration1; // first addEndpoint("/ws")
    @Mock
    private StompWebSocketEndpointRegistration wsRegistration2; // second addEndpoint("/ws") -> withSockJS()

    /**
     * Tests that the configureMessageBroker method sets up the simple broker and destination prefixes correctly.
     */
    @Test
    void configureMessageBroker_shouldSetSimpleBrokerAndPrefixes() {
        // Arrange
        WebSocketConfig config = new WebSocketConfig();

        // Act
        config.configureMessageBroker(messageBrokerRegistry);

        // Assert
        // Verify that the simple broker is enabled under "/queue"
        verify(messageBrokerRegistry).enableSimpleBroker("/queue");
        // Verify application destination prefix
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
        // Verify user destination prefix
        verify(messageBrokerRegistry).setUserDestinationPrefix("/user");
    }

    /**
     * Tests that the registerStompEndpoints method registers the "/ws" endpoint twice:
     * first without SockJS, then with SockJS enabled.
     */
    @Test
    void registerStompEndpoints_shouldRegisterWsEndpoint_twice_andEnableSockJsOnSecond() {
        // Arrange
        WebSocketConfig config = new WebSocketConfig();

        // Mock the fluent API: addEndpoint("/ws") returns a registration, and setAllowedOriginPatterns("*") returns same registration
        when(stompEndpointRegistry.addEndpoint("/ws")).thenReturn(wsRegistration1, wsRegistration2);
        when(wsRegistration1.setAllowedOriginPatterns("*")).thenReturn(wsRegistration1);
        when(wsRegistration2.setAllowedOriginPatterns("*")).thenReturn(wsRegistration2);

        // Act
        config.registerStompEndpoints(stompEndpointRegistry);

        // Assert
        // Verify that addEndpoint("/ws") is called twice (first plain, then SockJS)
        verify(stompEndpointRegistry, times(2)).addEndpoint("/ws");

        // First registration: only allowed origins are set
        verify(wsRegistration1).setAllowedOriginPatterns("*");
        verify(wsRegistration1, never()).withSockJS();

        // Second registration: allowed origins + SockJS fallback
        verify(wsRegistration2).setAllowedOriginPatterns("*");
        verify(wsRegistration2).withSockJS();
    }

}
