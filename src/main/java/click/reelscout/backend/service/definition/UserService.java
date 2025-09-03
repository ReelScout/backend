package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.User;

import java.util.List;

public interface UserService <U extends User, R extends UserRequestDTO, S extends UserResponseDTO> {
    List<S> getAll();

    S getById(Long id);

    S getByEmail(String email);

    S getByUsername(String username);

    S getByUsernameOrEmail(String usernameOrEmail);

    S getCurrentUserDto(U authenticatedUser);

    UserLoginResponseDTO update(U user, R userRequestDTO);

    CustomResponseDTO changePassword(U authenticatedUser, UserPasswordChangeRequestDTO userPasswordChangeRequestDTO);
}
