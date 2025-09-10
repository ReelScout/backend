package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.request.SuspendUserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import click.reelscout.backend.validation.Update;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("${api.paths.user}")
@RestController
public class UserController <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    private final UserService<U,R,S> userService;

    @GetMapping("/all")
    public ResponseEntity<List<S>> getAll() {
        List<S> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<S> getCurrentUser(@AuthenticationPrincipal U authenticatedUser) {
        S userResponseDTO = userService.getCurrentUserDto(authenticatedUser);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<S> getById(@PathVariable Long id) {
        S userResponseDTO = userService.getById(id);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<S> getByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        S userResponseDTO = userService.getByUsernameOrEmail(usernameOrEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<UserLoginResponseDTO> update(@AuthenticationPrincipal U authenticatedUser, @Validated(Update.class) @RequestBody R userRequestDTO) {
        UserLoginResponseDTO userResponseDTO = userService.update(authenticatedUser, userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/change-password")
    public ResponseEntity<CustomResponseDTO> changePassword(@AuthenticationPrincipal U authenticatedPrincipal, @Valid @RequestBody UserPasswordChangeRequestDTO userPasswordChangeRequestDTO) {
        CustomResponseDTO response = userService.changePassword(authenticatedPrincipal, userPasswordChangeRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PostMapping("/id/{id}/suspend")
    public ResponseEntity<CustomResponseDTO> suspendUser(@PathVariable Long id, @Valid @RequestBody SuspendUserRequestDTO dto) {
        CustomResponseDTO response = userService.suspendUser(id, dto.getUntil(), dto.getReason());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @DeleteMapping("/id/{id}/suspend")
    public ResponseEntity<CustomResponseDTO> unsuspendUser(@PathVariable Long id) {
        CustomResponseDTO response = userService.unsuspendUser(id);
        return ResponseEntity.ok(response);
    }
}
