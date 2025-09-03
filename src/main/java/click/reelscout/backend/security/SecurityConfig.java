package click.reelscout.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    protected static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                """
                    ROLE_ADMIN > ROLE_MODERATOR
                    ROLE_ADMIN > ROLE_PRODUCTION_COMPANY
                    ROLE_MODERATOR > ROLE_VERIFIED_MEMBER
                    ROLE_VERIFIED_MEMBER > ROLE_MEMBER
                """
        );
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(Environment environment) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(environment.getProperty("api.basic-path") + "/**", configuration);

        return source;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource(environment)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(environment.getProperty("api.paths.auth")+"/**").permitAll()
                .requestMatchers(environment.getProperty("api.paths.user")+"/**").authenticated()
                .requestMatchers(environment.getProperty("api.paths.content")+"/**").permitAll()
                .requestMatchers(environment.getProperty("api.paths.search")+"/**").permitAll()
                .anyRequest().denyAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) -> handlerExceptionResolver.resolveException(request, response, null, authException))
                    .accessDeniedHandler((request, response, accessDeniedException) -> handlerExceptionResolver.resolveException(request, response, null, accessDeniedException))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
