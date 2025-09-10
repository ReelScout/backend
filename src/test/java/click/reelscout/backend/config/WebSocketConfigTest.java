package click.reelscout.backend.config;

import click.reelscout.backend.websocket.StompAuthChannelInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketConfig.
 * These tests verify broker configuration, STOMP endpoint registration (with and without SockJS),
 * and that the custom ChannelInterceptor is attached to the inbound channel.
 */
@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration wsRegistration1; // first addEndpoint("/ws")
    @Mock
    private StompWebSocketEndpointRegistration wsRegistration2; // second addEndpoint("/ws") -> withSockJS()

    @Mock
    private ChannelRegistration channelRegistration;

    @Test
    void configureMessageBroker_shouldSetSimpleBrokerAndPrefixes() {
        // Arrange
        WebSocketConfig config = new WebSocketConfig(stompAuthChannelInterceptor);

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

    @Test
    void registerStompEndpoints_shouldRegisterWsEndpoint_twice_andEnableSockJsOnSecond() {
        // Arrange
        WebSocketConfig config = new WebSocketConfig(stompAuthChannelInterceptor);

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

    @Test
    void configureClientInboundChannel_shouldAttachAuthInterceptor() {
        // Arrange
        WebSocketConfig config = new WebSocketConfig(stompAuthChannelInterceptor);

        // Act
        config.configureClientInboundChannel(channelRegistration);

        // Assert
        // The method uses varargs; verify it gets called with our interceptor.
        verify(channelRegistration).interceptors(stompAuthChannelInterceptor);

        // (Optional) Capture to assert exact content of varargs
        ArgumentCaptor<org.springframework.messaging.support.ChannelInterceptor[]> captor =
                ArgumentCaptor.forClass(org.springframework.messaging.support.ChannelInterceptor[].class);
        verify(channelRegistration).interceptors(captor.capture());
        org.springframework.messaging.support.ChannelInterceptor[] passed = captor.getValue();
        assertArrayEquals(new org.springframework.messaging.support.ChannelInterceptor[]{stompAuthChannelInterceptor}, passed,
                "Inbound channel should be configured with the StompAuthChannelInterceptor only");
    }
}