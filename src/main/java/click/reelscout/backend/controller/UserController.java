package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;
import click.reelscout.backend.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${api.paths.user}")
@RestController
public class UserController <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    private final UserService<U,R,S> userService;

    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<UserResponseDTO> getByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        UserResponseDTO userResponseDTO = userService.getByUsernameOrEmail(usernameOrEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> update(@RequestBody R userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.update(userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }
}
