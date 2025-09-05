package click.reelscout.backend.config;

import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.repository.jpa.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ApplicationConfig.
 * <p>
 * Notes:
 * - We DO NOT load the Spring context (no @SpringBootTest, no @Configuration).
 * - We instantiate ApplicationConfig directly with a mocked UserRepository.
 * - We DO NOT test authenticationManager(AuthenticationConfiguration) here
 *   because it requires the Spring context; that belongs to integration tests.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class ApplicationConfigTest {

    @Test
    void userDetailsService_returnsUser_whenFound() {
        // Arrange: repository returns a user for the given username/email
        @SuppressWarnings("unchecked")
        UserRepository<User> repo = mock(UserRepository.class);
        User fakeUser = mock(User.class); // your domain User should implement UserDetails
        when(repo.findByUsernameOrEmail("test")).thenReturn(Optional.of(fakeUser));

        ApplicationConfig<User> config = new ApplicationConfig<>(repo);

        // Act
        UserDetailsService service = config.userDetailsService();

        // Assert
        assertSame(fakeUser, service.loadUserByUsername("test"), "Should return the exact user from repository");
        verify(repo).findByUsernameOrEmail("test");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void userDetailsService_throws_whenMissing() {
        // Arrange: repository returns empty
        @SuppressWarnings("unchecked")
        UserRepository<User> repo = mock(UserRepository.class);
        when(repo.findByUsernameOrEmail("missing")).thenReturn(Optional.empty());

        ApplicationConfig<User> config = new ApplicationConfig<>(repo);
        UserDetailsService service = config.userDetailsService();

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"),
                "Should throw UsernameNotFoundException when user is not found");
    }

    @Test
    void passwordEncoder_encodesAndMatches_roundtrip() {
        // Arrange
        ApplicationConfig<User> config = new ApplicationConfig<>(mock(UserRepository.class));
        PasswordEncoder encoder = config.passwordEncoder();

        // Act
        String raw = "secret";
        String encoded = encoder.encode(raw);

        // Assert
        assertNotEquals(raw, encoded, "Encoded password should differ from raw");
        assertTrue(encoder.matches(raw, encoded), "Encoder must verify the raw password against the hash");
    }

    @Test
    void authenticationProvider_supports_usernamePasswordToken() {
        // Arrange
        ApplicationConfig<User> config = new ApplicationConfig<>(mock(UserRepository.class));
        AuthenticationProvider provider = config.authenticationProvider();

        // Assert: DaoAuthenticationProvider supports UsernamePasswordAuthenticationToken
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticationProvider_authenticates_withValidCredentials() {
        // Arrange
        @SuppressWarnings("unchecked")
        UserRepository<User> repo = mock(UserRepository.class);
        ApplicationConfig<User> config = new ApplicationConfig<>(repo);

        // Build encoder & provider from our config (still unit, no Spring context)
        PasswordEncoder encoder = config.passwordEncoder();
        AuthenticationProvider provider = config.authenticationProvider();

        // Mock a domain User that implements UserDetails
        User user = mock(User.class);

        // Stub all UserDetails methods required by DaoAuthenticationProvider
        when(user.getUsername()).thenReturn("studio");
        // Store encoded password as it would be in DB
        when(user.getPassword()).thenReturn(encoder.encode("pwd123"));
        when(user.getAuthorities()).thenReturn(Collections.emptyList());
        when(user.isAccountNonExpired()).thenReturn(true);
        when(user.isAccountNonLocked()).thenReturn(true);
        when(user.isCredentialsNonExpired()).thenReturn(true);
        when(user.isEnabled()).thenReturn(true);

        // Repository must return our user for that lookup key
        when(repo.findByUsernameOrEmail("studio")).thenReturn(Optional.of(user));

        // Act: authenticate with correct credentials
        var token = new UsernamePasswordAuthenticationToken("studio", "pwd123");
        var result = provider.authenticate(token);

        // Assert
        assertNotNull(result);
        assertTrue(result.isAuthenticated(), "Resulting Authentication must be marked as authenticated");
        assertEquals("studio", result.getName());
        verify(repo).findByUsernameOrEmail("studio");
    }

    @Test
    void authenticationProvider_rejects_invalidPassword() {
        // Arrange
        @SuppressWarnings("unchecked")
        UserRepository<User> repo = mock(UserRepository.class);
        ApplicationConfig<User> config = new ApplicationConfig<>(repo);

        PasswordEncoder encoder = config.passwordEncoder();
        AuthenticationProvider provider = config.authenticationProvider();

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("studio");
        // Password stored is different from the one we will try
        when(user.getPassword()).thenReturn(encoder.encode("correct-password"));
        when(user.getAuthorities()).thenReturn(Collections.emptyList());
        when(user.isAccountNonExpired()).thenReturn(true);
        when(user.isAccountNonLocked()).thenReturn(true);
        when(user.isCredentialsNonExpired()).thenReturn(true);
        when(user.isEnabled()).thenReturn(true);

        when(repo.findByUsernameOrEmail("studio")).thenReturn(Optional.of(user));

        // Act & Assert: authenticating with a wrong password must fail
        var badToken = new UsernamePasswordAuthenticationToken("studio", "wrong-password");
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(badToken));
        verify(repo).findByUsernameOrEmail("studio");
    }
}