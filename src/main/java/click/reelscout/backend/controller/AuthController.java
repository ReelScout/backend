package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.service.definition.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("${api.paths.auth}")
@RestController
public class AuthController <R extends UserRequestDTO> {
    private final AuthService<R> authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return ResponseEntity.ok(authService.login(userLoginRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<UserLoginResponseDTO> register(@Valid @RequestBody R memberRequestDTO) {
        return ResponseEntity.ok(authService.register(memberRequestDTO));
    }
}
