package click.reelscout.backend.websocket;

import click.reelscout.backend.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StompAuthChannelInterceptor.
 */
@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;

    // Channel is not used by interceptor logic; keep it as a mock to satisfy the signature
    @Mock MessageChannel channel;

    @InjectMocks StompAuthChannelInterceptor interceptor;

    // ------------------------- helpers -------------------------

    /** Build a STOMP CONNECT message with provided native headers. */
    private static Message<byte[]> buildConnectMessage(java.util.Map<String, String> nativeHeaders) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        if (nativeHeaders != null) {
            nativeHeaders.forEach(accessor::setNativeHeader);
        }
        // Build the message using the accessor's headers
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    /** Re-extract the accessor from a Message, to inspect headers set by the interceptor. */
    private static StompHeaderAccessor accessorOf(Message<?> msg) {
        return StompHeaderAccessor.wrap(msg);
    }

    private static UserDetails userWithRoles(String username, String... roles) {
        java.util.List<GrantedAuthority> auths = java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
        return new User(username, "pwd", auths);
    }

    // ------------------------- tests -------------------------

    @Test
    @DisplayName("preSend(): if no STOMP accessor is present, returns the original message unchanged")
    void preSend_noAccessor_returnsOriginalMessage() {
        Message<String> msg = MessageBuilder.withPayload("noop").build();

        Message<?> out = interceptor.preSend(msg, channel);

        assertThat(out).isSameAs(msg);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("CONNECT: missing token -> throws IllegalArgumentException")
    void connect_missingToken_throwsIAE() {
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of()); // no Authorization / token

        assertThatThrownBy(() -> interceptor.preSend(msg, channel))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing or invalid Authorization");

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("CONNECT: lowercase 'authorization' header with Bearer token is accepted")
    void connect_lowercaseAuthorizationHeader_ok() {
        String rawToken = "jwt-token";
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of("authorization", "Bearer " + rawToken));

        UserDetails ud = userWithRoles("alice", "ROLE_MEMBER");
        when(jwtService.extractUsername(rawToken)).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(ud);
        when(jwtService.isTokenValid(rawToken, ud)).thenReturn(true);

        Message<?> out = interceptor.preSend(msg, channel);

        var acc = accessorOf(out);
        assertThat(acc.getUser()).as("Authentication should be set")
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);
        var auth = (UsernamePasswordAuthenticationToken) acc.getUser();
        Assertions.assertNotNull(auth);
        assertThat(auth.getPrincipal()).isSameAs(ud);
        assertThat(auth.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_MEMBER");
    }

    @Test
    @DisplayName("CONNECT: bare Authorization token (no 'Bearer') is accepted")
    void connect_bareAuthorizationToken_ok() {
        String rawToken = "jwt-token";
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of("Authorization", rawToken));

        UserDetails ud = userWithRoles("bob", "ROLE_VERIFIED_MEMBER");
        when(jwtService.extractUsername(rawToken)).thenReturn("bob");
        when(userDetailsService.loadUserByUsername("bob")).thenReturn(ud);
        when(jwtService.isTokenValid(rawToken, ud)).thenReturn(true);

        Message<?> out = interceptor.preSend(msg, channel);

        var acc = accessorOf(out);
        assertThat(acc.getUser()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        var auth = (UsernamePasswordAuthenticationToken) acc.getUser();
        Assertions.assertNotNull(auth);
        assertThat(auth.getPrincipal()).isSameAs(ud);
        assertThat(auth.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_VERIFIED_MEMBER");
    }

    @Test
    @DisplayName("CONNECT: token provided in 'token' header fallback is accepted")
    void connect_tokenHeader_ok() {
        String rawToken = "jwt";
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of("token", rawToken));

        UserDetails ud = userWithRoles("carol", "ROLE_MEMBER");
        when(jwtService.extractUsername(rawToken)).thenReturn("carol");
        when(userDetailsService.loadUserByUsername("carol")).thenReturn(ud);
        when(jwtService.isTokenValid(rawToken, ud)).thenReturn(true);

        Message<?> out = interceptor.preSend(msg, channel);

        var acc = accessorOf(out);
        assertThat(acc.getUser()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    @DisplayName("CONNECT: invalid token -> throws IllegalArgumentException")
    void connect_invalidToken_throws() {
        String token = "bad";
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of("Authorization", "Bearer " + token));

        UserDetails ud = userWithRoles("dee", "ROLE_MEMBER");
        when(jwtService.extractUsername(token)).thenReturn("dee");
        when(userDetailsService.loadUserByUsername("dee")).thenReturn(ud);
        when(jwtService.isTokenValid(token, ud)).thenReturn(false);

        assertThatThrownBy(() -> interceptor.preSend(msg, channel))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid JWT token");

        // Services were consulted
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("dee");
        verify(jwtService).isTokenValid(token, ud);
    }

    @Test
    @DisplayName("CONNECT: user without allowed roles -> throws SecurityException")
    void connect_forbiddenRole_throws() {
        String token = "ok";
        Message<byte[]> msg = buildConnectMessage(java.util.Map.of("Authorization", "Bearer " + token));

        UserDetails ud = userWithRoles("erin", "ROLE_PRODUCTION");
        when(jwtService.extractUsername(token)).thenReturn("erin");
        when(userDetailsService.loadUserByUsername("erin")).thenReturn(ud);
        when(jwtService.isTokenValid(token, ud)).thenReturn(true);

        assertThatThrownBy(() -> interceptor.preSend(msg, channel))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Only members can use chat");
    }

    @Test
    @DisplayName("Non-CONNECT command: interceptor is a no-op (no validation, no auth set)")
    void nonConnect_isNoop() {
        // Build a SEND message instead of CONNECT
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> out = interceptor.preSend(msg, channel);

        var acc = accessorOf(out);
        assertThat(acc.getUser()).isNull();
        verifyNoInteractions(jwtService, userDetailsService);
    }
}