package click.reelscout.backend.factory;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class UserMapperFactoryRegistry <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>, F extends UserMapperFactory<U, B, R, S, M>> {
    private final List<F> factories;

    public M getMapperFor(R userRequestDTO) {
        return findMapper(factory -> factory.supports(userRequestDTO));
    }

    public M getMapperFor(S userResponseDTO) {
        return findMapper(factory -> factory.supports(userResponseDTO));
    }

    public M getMapperFor(U user) {
        return findMapper(factory -> factory.supports(user));
    }

    private M findMapper(Predicate<F> condition) {
        return factories.stream()
                .filter(condition)
                .findFirst()
                .map(UserMapperFactory::createMapper)
                .orElseThrow(() -> new EntityCreateException("Invalid user type"));
    }
}