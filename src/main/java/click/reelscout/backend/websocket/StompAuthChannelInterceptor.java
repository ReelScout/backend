package click.reelscout.backend.websocket;

import click.reelscout.backend.security.JwtService;
import click.reelscout.backend.exception.custom.AccountSuspendedException;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Intercepts STOMP messages to authenticate users during the CONNECT phase using JWT tokens.
 * Validates the token, checks user roles, and ensures the account is not suspended.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Intercepts the STOMP CONNECT message to authenticate the user.
     *
     * @param message the incoming STOMP message
     * @param channel the message channel
     * @return the original message if authentication is successful
     * @throws IllegalArgumentException if the token is missing or invalid
     * @throws SecurityException        if the user does not have the required roles
     * @throws AccountSuspendedException if the user's account is suspended
     */
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extractToken(accessor);
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Missing or invalid Authorization for STOMP CONNECT");
            }
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                throw new IllegalArgumentException("Invalid JWT token for STOMP CONNECT");
            }

            if (userDetails instanceof User domainUser && (domainUser.getSuspendedUntil() != null && domainUser.getSuspendedUntil().isAfter(java.time.LocalDateTime.now()))) {
                boolean isPermanent = (domainUser.getSuspendedReason() != null && domainUser.getSuspendedReason().toLowerCase().contains("permanent"))
                        || (domainUser.getSuspendedUntil() != null && domainUser.getSuspendedUntil().getYear() >= 9999);
                String msg = isPermanent ? "Account permanently banned" : ("Account suspended until " + domainUser.getSuspendedUntil());
                if (domainUser.getSuspendedReason() != null && !domainUser.getSuspendedReason().isBlank()) {
                    msg += ": " + domainUser.getSuspendedReason();
                }
                throw new AccountSuspendedException(msg);
            }

            // Only allow members to chat (verified members included). Production companies excluded.
            if (!hasAnyRole(userDetails.getAuthorities(), List.of("ROLE_MEMBER", "ROLE_VERIFIED_MEMBER", "ROLE_ADMIN", "ROLE_MODERATOR"))) {
                throw new SecurityException("Only members can use chat");
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(auth);
        }
        return message;
    }

    /**
     * Extracts the JWT token from the STOMP headers.
     *
     * @param accessor the STOMP header accessor
     * @return the extracted JWT token, or null if not found
     */
    private String extractToken(StompHeaderAccessor accessor) {
        // Accept: Authorization: "Bearer <jwt>" OR Authorization: "<jwt>"
        // Fallback: native header "token": "<jwt>"
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header == null) header = accessor.getFirstNativeHeader("authorization");
        if (header != null && !header.isBlank()) {
            String h = header.trim();
            if (h.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return h.substring(7).trim();
            }
            return h; // bare token
        }
        String token = accessor.getFirstNativeHeader("token");
        return token == null ? null : token.trim();
    }

    /**
     * Checks if the user has any of the specified roles.
     *
     * @param authorities the collection of granted authorities
     * @param roles       the list of roles to check against
     * @return true if the user has at least one of the specified roles, false otherwise
     */
    private boolean hasAnyRole(Collection<? extends GrantedAuthority> authorities, List<String> roles) {
        for (GrantedAuthority a : authorities) {
            if (roles.contains(a.getAuthority())) return true;
        }
        return false;
    }
}
