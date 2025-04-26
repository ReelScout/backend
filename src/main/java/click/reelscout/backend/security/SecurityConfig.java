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
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                """
                    ROLE_ADMIN > ROLE_MODERATOR
                    ROLE_ADMIN > ROLE_PRODUCTION
                    ROLE_MODERATOR > ROLE_VERIFIED_USER
                    ROLE_VERIFIED_USER > ROLE_USER
                """
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(environment.getProperty("api.paths.auth")+"/**").permitAll()
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
