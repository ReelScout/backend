package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class UserMapperFactoryRegistry {
    private final List<UserMapperFactory> factories;

    public UserMapper getMapperFor(UserRequestDTO userRequestDTO) {
        return findMapper(factory -> factory.supports(userRequestDTO));
    }

    public UserMapper getMapperFor(UserResponseDTO userResponseDTO) {
        return findMapper(factory -> factory.supports(userResponseDTO));
    }

    public UserMapper getMapperFor(User user) {
        return findMapper(factory -> factory.supports(user));
    }

    private UserMapper findMapper(Predicate<UserMapperFactory> condition) {
        return factories.stream()
                .filter(condition)
                .findFirst()
                .map(UserMapperFactory::createMapper)
                .orElseThrow(() -> new EntityCreateException("Invalid user type"));
    }
}