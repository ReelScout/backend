package click.reelscout.backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityConfig class.
 * Focuses on role hierarchy and CORS configuration behavior.
 */
class SecurityConfigTest {

    private final SecurityConfig config =
            new SecurityConfig(/* JwtFilter */ null, /* HandlerExceptionResolver */ null);

    @Test
    @DisplayName("roleHierarchy(): returns a RoleHierarchyImpl instance")
    void roleHierarchy_returnsHierarchy() {
        RoleHierarchy rh = SecurityConfig.roleHierarchy();
        assertNotNull(rh);
        assertInstanceOf(RoleHierarchyImpl.class, rh, "Expected the concrete implementation used in the config");
    }

    /**
     * Tests that the CORS configuration source registers the expected path
     * and exposes the correct CORS settings.
     */
    @Test
    @DisplayName("corsConfigurationSource(): registers config under api.basic-path and exposes expected values")
    void corsConfigurationSource_registersAndExposesValues() {
        // Arrange: simulate property api.basic-path=/api
        MockEnvironment env = new MockEnvironment().withProperty("api.basic-path", "/api");

        CorsConfigurationSource source = config.corsConfigurationSource(env);
        assertNotNull(source);

        // Request that matches the registered path (/api/**)
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/content/all");

        // Act
        CorsConfiguration cc = source.getCorsConfiguration(req);

        // Assert: configuration is returned for matching path and has expected settings
        assertNotNull(cc, "CORS configuration must be applied for /api/** requests");
        assertNotNull(cc.getAllowedOrigins());
        assertTrue(cc.getAllowedOrigins().contains("http://localhost:8081"));
        assertNotNull(cc.getAllowedMethods());
        assertTrue(cc.getAllowedMethods().containsAll(
                java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")));
        assertNotNull(cc.getAllowedHeaders());
        assertTrue(cc.getAllowedHeaders().contains("*"));
        assertEquals(Boolean.TRUE, cc.getAllowCredentials());
    }

    /**
     * Tests that requests outside the configured base path do not receive any CORS configuration.
     * This ensures that CORS settings are scoped correctly.
     */
    @Test
    @DisplayName("corsConfigurationSource(): returns null for non-matching paths")
    void corsConfigurationSource_nonMatchingPath_returnsNull() {
        MockEnvironment env = new MockEnvironment().withProperty("api.basic-path", "/api");
        CorsConfigurationSource source = config.corsConfigurationSource(env);

        // A request outside /api/**
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/public/ping");

        CorsConfiguration cc = source.getCorsConfiguration(req);
        assertNull(cc, "No CORS config should apply outside /api/**");
    }

    /**
     * Tests the role hierarchy to ensure that higher roles imply the permissions of lower roles.
     * Specifically checks that ADMIN implies MODERATOR, VERIFIED_MEMBER, and MEMBER.
     */
    @Test
    @DisplayName("roleHierarchy(): ADMIN implies MODERATOR, VERIFIED_MEMBER and MEMBER")
    void roleHierarchy_resolvesImpliedRoles() {
        var rh = SecurityConfig.roleHierarchy();

        var adminAuths = rh.getReachableGrantedAuthorities(
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // Deve contenere tutte le autorità implicate dalla gerarchia
        assertTrue(adminAuths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(adminAuths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR")));
        assertTrue(adminAuths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIED_MEMBER")));
        assertTrue(adminAuths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER")));
    }

    /**
     * Tests that MODERATOR implies VERIFIED_MEMBER and MEMBER, but not ADMIN.
     */
    @Test
    @DisplayName("roleHierarchy(): MODERATOR implies VERIFIED_MEMBER and MEMBER (but not ADMIN)")
    void roleHierarchy_moderatorImplications() {
        var rh = SecurityConfig.roleHierarchy();
        var auths = rh.getReachableGrantedAuthorities(
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MODERATOR"))
        );

        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR")));
        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIED_MEMBER")));
        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER")));
        assertFalse(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    /**
     * Tests that VERIFIED_MEMBER implies MEMBER, but not MODERATOR or ADMIN.
     */
    @Test
    @DisplayName("roleHierarchy(): VERIFIED_MEMBER implies MEMBER only")
    void roleHierarchy_verifiedMemberImplications() {
        var rh = SecurityConfig.roleHierarchy();
        var auths = rh.getReachableGrantedAuthorities(
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_VERIFIED_MEMBER"))
        );

        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIED_MEMBER")));
        assertTrue(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER")));
        assertFalse(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR")));
        assertFalse(auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    /**
     * Tests that MEMBER does not imply any other roles.
     */
    @Test
    @DisplayName("corsConfigurationSource(): exact values for origins/methods/headers/credentials")
    void corsConfigurationSource_exactValues() {
        MockEnvironment env = new MockEnvironment().withProperty("api.basic-path", "/api");
        CorsConfigurationSource source = config.corsConfigurationSource(env);
        var req = new MockHttpServletRequest("OPTIONS", "/api/anything");
        CorsConfiguration cc = source.getCorsConfiguration(req);

        assertNotNull(cc);
        assertEquals(java.util.List.of("http://localhost:8081"), cc.getAllowedOrigins(), "Single allowed origin");
        assertEquals(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"), cc.getAllowedMethods(), "Allowed methods");
        assertEquals(java.util.List.of("*"), cc.getAllowedHeaders(), "Wildcard header");
        assertEquals(Boolean.TRUE, cc.getAllowCredentials());
    }

    /**
     * Tests that changing the base path in the environment property
     * correctly updates the CORS configuration source to match the new path.
     */
    @Test
    @DisplayName("corsConfigurationSource(): different base path still matches correctly")
    void corsConfigurationSource_differentBasePath() {
        MockEnvironment env = new MockEnvironment().withProperty("api.basic-path", "/v1");
        CorsConfigurationSource source = config.corsConfigurationSource(env);

        var match = new MockHttpServletRequest("GET", "/v1/content/all");
        var miss  = new MockHttpServletRequest("GET", "/api/content/all");

        assertNotNull(source.getCorsConfiguration(match), "should match /v1/**");
        assertNull(source.getCorsConfiguration(miss), "should NOT match /api/**");
    }
}