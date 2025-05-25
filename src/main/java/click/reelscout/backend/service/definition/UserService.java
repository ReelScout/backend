package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getByEmail(String email);

    UserResponseDTO getByUsername(String username);

    UserResponseDTO getByUsernameOrEmail(String usernameOrEmail);
}
