package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;

public interface UserMapperFactory {
    boolean supports(UserRequestDTO userRequestDTO);
    boolean supports(User user);
    UserMapper createMapper();
}