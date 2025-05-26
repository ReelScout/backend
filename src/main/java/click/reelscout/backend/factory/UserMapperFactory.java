package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.mapper.definition.UserMapper;

public interface UserMapperFactory {
    boolean supports(UserRequestDTO userRequestDTO);
    UserMapper createMapper();
}