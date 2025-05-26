package click.reelscout.backend.factory;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.mapper.definition.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapperFactoryRegistry {
    private final List<UserMapperFactory> factories;

    public UserMapper getMapperFor(UserRequestDTO userRequestDTO) {
        return factories.stream()
                .filter(factory -> factory.supports(userRequestDTO))
                .findFirst()
                .map(UserMapperFactory::createMapper)
                .orElseThrow(() -> new EntityCreateException("Invalid user type"));
    }
}