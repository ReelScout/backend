package click.reelscout.backend.websocket;

import click.reelscout.backend.model.jpa.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                .nullDestMatcher().authenticated()
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT).authenticated()
                .simpTypeMatchers(SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE).hasRole(Role.MEMBER.name())
                .simpTypeMatchers(SimpMessageType.MESSAGE).hasRole(Role.MEMBER.name())
                .anyMessage().denyAll()
                .build();
    }
}