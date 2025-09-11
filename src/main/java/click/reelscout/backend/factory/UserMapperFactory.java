package click.reelscout.backend.factory;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.User;

/**
 * Factory interface for creating UserMapper instances.
 *
 * @param <U> the type of User
 * @param <B> the type of UserBuilder
 * @param <R> the type of UserRequestDTO
 * @param <S> the type of UserResponseDTO
 * @param <M> the type of UserMapper
 */
public interface UserMapperFactory <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>> {
    /**
     * Checks if the factory supports the given type.
     *
     * @param userRequestDTO the UserRequestDTO to check
     * @return true if supported, false otherwise
     */
    boolean supports(R userRequestDTO);

    /**
     * Checks if the factory supports the given type.
     *
     * @param userResponseDTO the UserResponseDTO to check
     * @return true if supported, false otherwise
     */
    boolean supports(S userResponseDTO);

    /**
     * Checks if the factory supports the given type.
     *
     * @param user the User to check
     * @return true if supported, false otherwise
     */
    boolean supports(U user);

    /**
     * Creates a new instance of UserMapper.
     *
     * @return a new UserMapper instance
     */
    M createMapper();
}