package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;

public interface UserService <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    S getByEmail(String email);

    S getByUsername(String username);

    S getByUsernameOrEmail(String usernameOrEmail);

    U getCurrentUser();

    S getCurrentUserDto();

    S update(R userRequestDTO);

    CustomResponseDTO changePassword(UserPasswordChangeRequestDTO userPasswordChangeRequestDTO);
}
