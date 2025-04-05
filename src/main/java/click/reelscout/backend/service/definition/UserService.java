package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO create(UserRequestDTO userRequestDTO);

    UserResponseDTO getByEmail(String email);

    UserResponseDTO getByUsername(String username);
}
