package click.reelscout.backend.factory;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * Registry for UserMapperFactory instances.
 * It provides methods to retrieve the appropriate UserMapper based on the type of user, request DTO, or response DTO.
 *
 * @param <U> the type of User
 * @param <B> the type of UserBuilder
 * @param <R> the type of UserRequestDTO
 * @param <S> the type of UserResponseDTO
 * @param <M> the type of UserMapper
 * @param <F> the type of UserMapperFactory
 */
@Component
@RequiredArgsConstructor
public class UserMapperFactoryRegistry <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>, F extends UserMapperFactory<U, B, R, S, M>> {
    private final List<F> factories;

    /**
     * Retrieves the appropriate UserMapper based on the provided UserRequestDTO.
     *
     * @param userRequestDTO the UserRequestDTO to find a mapper for
     * @return the corresponding UserMapper
     * @throws EntityCreateException if no suitable mapper is found
     */
    public M getMapperFor(R userRequestDTO) {
        return findMapper(factory -> factory.supports(userRequestDTO));
    }

    /**
     * Retrieves the appropriate UserMapper based on the provided UserResponseDTO.
     *
     * @param userResponseDTO the UserResponseDTO to find a mapper for
     * @return the corresponding UserMapper
     * @throws EntityCreateException if no suitable mapper is found
     */
    public M getMapperFor(S userResponseDTO) {
        return findMapper(factory -> factory.supports(userResponseDTO));
    }

    /**
     * Retrieves the appropriate UserMapper based on the provided User entity.
     *
     * @param user the User entity to find a mapper for
     * @return the corresponding UserMapper
     * @throws EntityCreateException if no suitable mapper is found
     */
    public M getMapperFor(U user) {
        return findMapper(factory -> factory.supports(user));
    }

    /**
     * Finds a UserMapper that matches the given condition.
     *
     * @param condition the predicate to match factories
     * @return the corresponding UserMapper
     * @throws EntityCreateException if no suitable mapper is found
     */
    private M findMapper(Predicate<F> condition) {
        return factories.stream()
                .filter(condition)
                .findFirst()
                .map(UserMapperFactory::createMapper)
                .orElseThrow(() -> new EntityCreateException("Invalid user type"));
    }
}