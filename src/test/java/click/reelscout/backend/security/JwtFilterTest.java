package click.reelscout.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private HandlerExceptionResolver resolver;
    private JwtFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        // Create mocks for dependencies
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);
        resolver = mock(HandlerExceptionResolver.class);

        // Inject mocks into JwtFilter
        filter = new JwtFilter(jwtService, userDetailsService, resolver);

        // Mock the FilterChain
        chain = mock(FilterChain.class);

        // Clear the SecurityContext before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void noHeader_passThrough() throws Exception {
        // Request without "Authorization" header
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();

        filter.doFilterInternal(req, res, chain);

        // Should just continue the filter chain, no authentication set
        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // No interactions with jwtService, userDetailsService or resolver
        verifyNoInteractions(jwtService, userDetailsService, resolver);
    }

    @Test
    void notBearerHeader_passThrough() throws Exception {
        // Request with header, but not starting with "Bearer"
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Basic abc");
        var res = new MockHttpServletResponse();

        filter.doFilterInternal(req, res, chain);

        // Should just continue, no authentication
        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, userDetailsService, resolver);
    }

    @Test
    void validBearer_setsAuthentication() throws Exception {
        // Request with a valid "Bearer " token
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer token123");
        var res = new MockHttpServletResponse();

        // Mock JWT behavior
        when(jwtService.extractUsername("token123")).thenReturn("alice");
        var details = new User("alice","x", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(details);
        when(jwtService.isTokenValid("token123", details)).thenReturn(true);

        filter.doFilterInternal(req, res, chain);

        // SecurityContext should now contain authentication for "alice"
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("alice", ((UserDetails) auth.getPrincipal()).getUsername());
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, auth);

        // Chain should always be continued
        verify(chain).doFilter(req, res);
    }

    @Test
    void invalidToken_doesNotAuthenticate() throws ServletException, IOException {
        // Request with a token that fails validation
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer bad");
        var res = new MockHttpServletResponse();

        when(jwtService.extractUsername("bad")).thenReturn("bob");
        var details = new User("bob","x", List.of());
        when(userDetailsService.loadUserByUsername("bob")).thenReturn(details);
        when(jwtService.isTokenValid("bad", details)).thenReturn(false);

        filter.doFilterInternal(req, res, chain);

        // Authentication should not be set in context
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    @Test
    void exception_resolvedByHandler() {
        // Request where extracting username throws an exception
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer crash");
        var res = new MockHttpServletResponse();

        when(jwtService.extractUsername("crash")).thenThrow(new RuntimeException("boom"));

        filter.doFilterInternal(req, res, chain);

        // Exception should be resolved by handlerExceptionResolver
        verify(resolver).resolveException(eq(req), eq(res), isNull(), ArgumentMatchers.any(RuntimeException.class));
        // Chain call depends on resolver behavior, so not asserted here
    }

    @Test
    void authenticationAlreadyPresent_skipSettingAgain() throws Exception {
        // Simulate an existing authentication in context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("pre", null, List.of())
        );

        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer token123");
        var res = new MockHttpServletResponse();

        when(jwtService.extractUsername("token123")).thenReturn("alice");

        filter.doFilterInternal(req, res, chain);

        // Should not reload user or validate token, since auth is already present
        verifyNoInteractions(userDetailsService);
        verify(jwtService).extractUsername("token123");
        verify(chain).doFilter(req, res);
    }
}