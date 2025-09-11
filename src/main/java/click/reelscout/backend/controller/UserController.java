package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.request.SuspendUserRequestDTO;
import click.reelscout.backend.dto.request.BanUserRequestDTO;
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

/**
 * Controller for handling user-related operations such as retrieval, update,
 * password change, suspension, banning, and moderator actions.
 * <p>
 * Generic Parameters:
 * <ul>
 *   <li>U - extends {@link click.reelscout.backend.model.jpa.User}, representing the user entity</li>
 *   <li>R - extends {@link click.reelscout.backend.dto.request.UserRequestDTO}, representing the request DTO for user updates</li>
 *   <li>S - extends {@link click.reelscout.backend.dto.response.UserResponseDTO}, representing the user response DTO</li>
 * </ul>
 */
@RequiredArgsConstructor
@RequestMapping("${api.paths.user}")
@RestController
public class UserController <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    private final UserService<U,R,S> userService;

    /**
     * Retrieves all users.
     * <p>Authorization: No specific authorization required.</p>
     * @return a ResponseEntity containing a list of user response DTOs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<S>> getAll() {
        List<S> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves the details of the current authenticated user.
     * <p>Authorization: Requires an authenticated user (<code>@PreAuthorize("isAuthenticated()")</code>).
     * @param authenticatedUser the currently authenticated user.
     * @return a ResponseEntity containing the user response DTO.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<S> getCurrentUser(@AuthenticationPrincipal U authenticatedUser) {
        S userResponseDTO = userService.getCurrentUserDto(authenticatedUser);
        return ResponseEntity.ok(userResponseDTO);
    }

    /**
     * Retrieves user details by user ID.
     * <p>Authorization: No specific authorization required.</p>
     * @param id the user's ID.
     * @return a ResponseEntity containing the user response DTO.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<S> getById(@PathVariable Long id) {
        S userResponseDTO = userService.getById(id);
        return ResponseEntity.ok(userResponseDTO);
    }

    /**
     * Retrieves user details by username or email.
     * <p>Authorization: No specific authorization required.</p>
     * @param usernameOrEmail the username or email.
     * @return a ResponseEntity containing the user response DTO.
     */
    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<S> getByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        S userResponseDTO = userService.getByUsernameOrEmail(usernameOrEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    /**
     * Updates the authenticated user's information.
     * <p>Authorization: Requires an authenticated user (<code>@PreAuthorize("isAuthenticated()")</code>).
     * @param authenticatedUser the currently authenticated user.
     * @param userRequestDTO the DTO containing update details.
     * @return a ResponseEntity containing the user login response DTO.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<UserLoginResponseDTO> update(@AuthenticationPrincipal U authenticatedUser, @Validated(Update.class) @RequestBody R userRequestDTO) {
        UserLoginResponseDTO userResponseDTO = userService.update(authenticatedUser, userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    /**
     * Changes the password for the authenticated user.
     * <p>Authorization: Requires an authenticated user (<code>@PreAuthorize("isAuthenticated()")</code>).
     * @param authenticatedPrincipal the currently authenticated user.
     * @param userPasswordChangeRequestDTO the DTO with password change details.
     * @return a ResponseEntity containing a custom response DTO.
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/change-password")
    public ResponseEntity<CustomResponseDTO> changePassword(@AuthenticationPrincipal U authenticatedPrincipal, @Valid @RequestBody UserPasswordChangeRequestDTO userPasswordChangeRequestDTO) {
        CustomResponseDTO response = userService.changePassword(authenticatedPrincipal, userPasswordChangeRequestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Suspends a user for a specified duration.
     * <p>Authorization: Requires a moderator (<code>@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")</code>).
     * @param id the user's ID.
     * @param dto the DTO containing suspension details.
     * @return a ResponseEntity containing a custom response DTO.
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @PostMapping("/id/{id}/suspend")
    public ResponseEntity<CustomResponseDTO> suspendUser(@PathVariable Long id, @Valid @RequestBody SuspendUserRequestDTO dto) {
        CustomResponseDTO response = userService.suspendUser(id, dto.getUntil(), dto.getReason());
        return ResponseEntity.ok(response);
    }

    /**
     * Reverses the suspension on a user.
     * <p>Authorization: Requires a moderator (<code>@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")</code>).
     * @param id the user's ID.
     * @return a ResponseEntity containing a custom response DTO.
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).MODERATOR)")
    @DeleteMapping("/id/{id}/suspend")
    public ResponseEntity<CustomResponseDTO> unsuspendUser(@PathVariable Long id) {
        CustomResponseDTO response = userService.unsuspendUser(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Permanently bans a user.
     * <p>Authorization: Requires an administrator (<code>@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")</code>).
     * @param admin the administrator performing the ban.
     * @param id the user's ID.
     * @param dto an optional DTO containing ban reason.
     * @return a ResponseEntity containing a custom response DTO.
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @PostMapping("/id/{id}/ban")
    public ResponseEntity<CustomResponseDTO> permanentlyBan(@AuthenticationPrincipal U admin, @PathVariable Long id, @RequestBody(required = false) BanUserRequestDTO dto) {
        String reason = (dto != null) ? dto.getReason() : null;
        CustomResponseDTO response = userService.permanentlyBanUser(id, admin, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Unbans a previously banned user.
     * <p>Authorization: Requires an administrator (<code>@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")</code>).
     * @param admin the administrator performing the unban.
     * @param id the user's ID.
     * @param dto an optional DTO containing unban reason.
     * @return a ResponseEntity containing a custom response DTO.
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @DeleteMapping("/id/{id}/ban")
    public ResponseEntity<CustomResponseDTO> unban(@AuthenticationPrincipal U admin, @PathVariable Long id, @RequestBody(required = false) BanUserRequestDTO dto) {
        String reason = (dto != null) ? dto.getReason() : null;
        CustomResponseDTO response = userService.unbanUser(id, admin, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists users reported by moderators.
     * <p>Authorization: Requires an administrator (<code>@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")</code>).
     * @return a ResponseEntity containing a list of user response DTOs.
     */
    @PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).ADMIN)")
    @GetMapping("/reported/moderator")
    public ResponseEntity<List<S>> listUsersReportedByModerators() {
        List<S> users = userService.listUsersReportedByModerators();
        return ResponseEntity.ok(users);
    }
}
