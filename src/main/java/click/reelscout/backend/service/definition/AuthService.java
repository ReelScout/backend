package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;

public interface AuthService <R extends UserRequestDTO> {
    UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO);

    UserLoginResponseDTO register(R userRequestDTO);
}
