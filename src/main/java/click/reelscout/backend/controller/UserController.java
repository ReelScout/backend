package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${api.paths.user}")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<UserResponseDTO> getByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        UserResponseDTO userResponseDTO = userService.getByUsernameOrEmail(usernameOrEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponseDTO> update(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.update(userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }
}
