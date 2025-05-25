package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;

public interface UserMapper<U extends User, R extends UserRequestDTO, S extends UserResponseDTO, B extends UserBuilder<U, B>, T extends EntityMapper<U, R, S, B, T>> extends EntityMapper<U, R, S, B, T> {
}