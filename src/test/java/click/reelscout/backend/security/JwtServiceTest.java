package click.reelscout.backend.security;

import click.reelscout.backend.builder.implementation.MemberBuilderImplementation;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.security.Key;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private Member testUser;

    // 32 bytes secret (256-bit) Base64-encoded for HS256
    private String base64Secret;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Create a deterministic 32-byte secret and encode to Base64
        byte[] raw = new byte[32];
        for (int i = 0; i < raw.length; i++) raw[i] = (byte) (i + 1);
        base64Secret = Base64.getEncoder().encodeToString(raw);

        // Inject fields annotated with @Value via reflection
        setField(jwtService, "secretKey", base64Secret);
        setField(jwtService, "jwtExpiration", 60_000L); // 60 seconds by default

        // Build a concrete Member user
        MemberBuilderImplementation builder = new MemberBuilderImplementation();
        builder.id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("password")
                .role(Role.MEMBER)
                .s3ImageKey(null)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1));
        testUser = builder.build();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    @DisplayName("generateToken should create a signed token containing subject and custom claims")
    void testGenerateTokenAndExtract() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);

        // Subject must be the username
        String subject = jwtService.extractUsername(token);
        assertEquals(testUser.getUsername(), subject);

        // Extract claims directly and verify the presence of our custom ones
        Claims claims = extractClaimsWithSameKey(token, base64Secret);
        assertEquals(testUser.getEmail(), claims.get("email"));
        assertEquals(testUser.getId(), ((Number) claims.get("id")).longValue());
        assertEquals(testUser.getRole().name(), claims.get("role"));
        assertEquals(1, ((Number) claims.get("tokenVersion")).intValue());

        // Expiration should be in the future (given default 60s in setup)
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    @DisplayName("isTokenValid should return true for a valid token and matching user")
    void testIsTokenValidTrue() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("isTokenValid should return false when username does not match")
    void testIsTokenValidWrongUser() {
        String token = jwtService.generateToken(testUser);

        // Create another user with a different username
        MemberBuilderImplementation builder = new MemberBuilderImplementation();
        builder.id(2L)
                .username("jane_doe")
                .email("jane@example.com")
                .password("password")
                .role(Role.MEMBER)
                .s3ImageKey(null)
                .firstName("Jane")
                .lastName("Doe")
                .birthDate(LocalDate.of(1992, 2, 2));
        Member otherUser = builder.build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    @DisplayName("isTokenValid should return false for an expired token")
    void testIsTokenValidExpired() throws Exception {
        // Temporarily set expiration to negative to generate an already expired token
        setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(testUser);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, testUser));

        // Restore reasonable expiration for isolation of further tests
        setField(jwtService, "jwtExpiration", 60_000L);
    }

    @Test
    @DisplayName("isTokenValid should return false for token signed with a different key (invalid signature)")
    void testIsTokenValidInvalidSignature() {
        // Build a token for the same subject but with a different signing key
        String otherSecret = Base64.getEncoder().encodeToString("another-secret-key-32-bytes-long!!".getBytes());
        Key wrongKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(otherSecret));

        Map<String, Object> claims = Map.of(
                "id", testUser.getId(),
                "email", testUser.getEmail(),
                "role", testUser.getRole(),
                "tokenVersion", 1
        );

        String badToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(testUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60_000L))
                .signWith(wrongKey)
                .compact();

        assertThrows(SignatureException.class, () -> jwtService.isTokenValid(badToken, testUser));
    }

    // Helper to parse claims without using JwtService internals for validation
    private static Claims extractClaimsWithSameKey(String token, String base64Secret) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
