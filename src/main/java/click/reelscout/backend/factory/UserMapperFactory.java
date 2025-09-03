package click.reelscout.backend.factory;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.User;

public interface UserMapperFactory <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>> {
    boolean supports(R userRequestDTO);
    boolean supports(S userResponseDTO);
    boolean supports(U user);
    M createMapper();
}