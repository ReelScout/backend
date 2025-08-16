package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;
import click.reelscout.backend.service.definition.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${api.paths.user}")
@RestController
public class UserController <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    private final UserService<U,R,S> userService;

    @GetMapping("/me")
    public ResponseEntity<S> getCurrentUser(@AuthenticationPrincipal U authenticatedUser) {
        S userResponseDTO = userService.getCurrentUserDto(authenticatedUser);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<S> getById(@PathVariable Long id) {
        S userResponseDTO = userService.getById(id);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<S> getByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        S userResponseDTO = userService.getByUsernameOrEmail(usernameOrEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> update(@AuthenticationPrincipal U authenticatedUser, @Valid @RequestBody R userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.update(authenticatedUser, userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<CustomResponseDTO> changePassword(@AuthenticationPrincipal U authenticatedPrincipal, @Valid @RequestBody UserPasswordChangeRequestDTO userPasswordChangeRequestDTO) {
        CustomResponseDTO response = userService.changePassword(authenticatedPrincipal, userPasswordChangeRequestDTO);
        return ResponseEntity.ok(response);
    }
}
