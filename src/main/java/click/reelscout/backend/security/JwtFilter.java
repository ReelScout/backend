package click.reelscout.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.LocalDateTime;
import click.reelscout.backend.exception.custom.AccountSuspendedException;
import click.reelscout.backend.model.jpa.User;

/**
 * Filter that intercepts incoming HTTP requests to validate JWT tokens and set the authentication context.
 */
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Filters incoming requests to validate JWT tokens and set the authentication context.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the filter chain to pass the request and response to the next filter
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            final String jwtToken;
            final String username;

            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwtToken = authorizationHeader.substring("Bearer ".length());
            username = jwtService.extractUsername(jwtToken);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails instanceof User domainUser && (domainUser.getSuspendedUntil() != null && domainUser.getSuspendedUntil().isAfter(LocalDateTime.now()))) {
                    boolean isPermanent = (domainUser.getSuspendedReason() != null && domainUser.getSuspendedReason().toLowerCase().contains("permanent"))
                            || (domainUser.getSuspendedUntil() != null && domainUser.getSuspendedUntil().getYear() >= 9999);
                    String msg = isPermanent ? "Account permanently banned" : ("Account suspended until " + domainUser.getSuspendedUntil());
                    if (domainUser.getSuspendedReason() != null && !domainUser.getSuspendedReason().isBlank()) {
                        msg += ": " + domainUser.getSuspendedReason();
                    }
                    throw new AccountSuspendedException(msg);
                }

                if(jwtService.isTokenValid(jwtToken, userDetails)) {
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
