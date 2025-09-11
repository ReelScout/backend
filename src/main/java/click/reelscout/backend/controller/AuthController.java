package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.service.definition.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import click.reelscout.backend.validation.Create;

/**
 * REST controller handling authentication operations such as login and registration.
 */
@RequiredArgsConstructor
@RequestMapping("${api.paths.auth}")
@RestController
public class AuthController <R extends UserRequestDTO> {
    private final AuthService<R> authService;

    /**
     * Authenticates a user with email/username and password.
     *
     * @param userLoginRequestDTO the user's login request data
     * @return a response containing user login details
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return ResponseEntity.ok(authService.login(userLoginRequestDTO));
    }

    /**
     * Registers a new user with the provided registration DTO.
     * The DTO is validated with the {@link Create} validation group.
     *
     * @param memberRequestDTO the user registration request data
     * @return a response containing user login details after registration
     */
    @PostMapping("/register")
    public ResponseEntity<UserLoginResponseDTO> register(@Validated(Create.class) @RequestBody R memberRequestDTO) {
        return ResponseEntity.ok(authService.register(memberRequestDTO));
    }
}
