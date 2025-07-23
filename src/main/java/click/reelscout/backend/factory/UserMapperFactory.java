package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;

@SuppressWarnings("rawtypes")
public interface UserMapperFactory {
    boolean supports(UserRequestDTO userRequestDTO);
    boolean supports(UserResponseDTO userResponseDTO);
    boolean supports(User user);
    UserMapper createMapper();
}