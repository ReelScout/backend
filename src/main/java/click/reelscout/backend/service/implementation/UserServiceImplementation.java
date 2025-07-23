package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.service.definition.UserService;
import click.reelscout.backend.strategy.UserMapperContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserMapperContext userMapperContext;
    private final UserMapperFactoryRegistry userMapperFactoryRegistry;

    @Override
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        return userMapperContext.toDto(user);
    }

    @Override
    public UserResponseDTO getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        return userMapperContext.toDto(user);
    }

    @Override
    public UserResponseDTO getByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class));

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(user));

        return userMapperContext.toDto(user);
    }
}
