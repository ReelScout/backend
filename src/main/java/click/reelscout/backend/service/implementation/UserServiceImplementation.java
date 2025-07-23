package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.exception.custom.InvalidCredentialsException;
import click.reelscout.backend.factory.UserMapperFactoryRegistry;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.service.definition.UserService;
import click.reelscout.backend.strategy.UserMapperContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserMapperContext userMapperContext;
    private final UserMapperFactoryRegistry userMapperFactoryRegistry;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new EntityNotFoundException(User.class);
        }

        return user;
    }

    @Override
    public UserResponseDTO getCurrentUserDto() {
        User currentUser = getCurrentUser();

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(currentUser));

        return userMapperContext.toDto(currentUser);
    }

    @Override
    public UserResponseDTO update(UserRequestDTO userRequestDTO) {
        User currentUser = getCurrentUser();

        if(!passwordEncoder.matches(userRequestDTO.getPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if(userRepository.existsByEmailAndIdIsNot(userRequestDTO.getEmail(), currentUser.getId())) {
            throw new EntityUpdateException("Email already in use");
        }

        if(userRepository.existsByUsernameAndIdIsNot(userRequestDTO.getUsername(), currentUser.getId())) {
            throw new EntityUpdateException("Username already in use");
        }

        userMapperContext.setUserMapper(userMapperFactoryRegistry.getMapperFor(currentUser));

        User user = userMapperContext.toEntity(userRequestDTO);

        User updatedUser = (User) userMapperContext.toBuilder(user).id(currentUser.getId()).build();

        try {
            userRepository.save(updatedUser);
        } catch (Exception e) {
            throw new EntityUpdateException(User.class);
        }

        return userMapperContext.toDto(updatedUser);
    }
}
