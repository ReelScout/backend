package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;

public interface UserService {
    UserResponseDTO getByEmail(String email);

    UserResponseDTO getByUsername(String username);

    UserResponseDTO getByUsernameOrEmail(String usernameOrEmail);

    User getCurrentUser();

    UserResponseDTO getCurrentUserDto();

    UserResponseDTO update(UserRequestDTO userRequestDTO);
}
