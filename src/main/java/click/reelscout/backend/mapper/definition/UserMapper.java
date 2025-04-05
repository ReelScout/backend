package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;

public interface UserMapper {
    UserResponseDTO toDto(User user);

    UserBuilder toBuilder(User user);

    User toEntity(UserRequestDTO userRequestDTO);
}
