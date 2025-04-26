package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUsernameOrEmail(userRequestDTO.getUsername(), userRequestDTO.getEmail())) {
            throw new EntityCreateException("User already exists");
        }

        User savedUser = userRepository.save(userMapper.toEntity(userRequestDTO));

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDTO getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        return userMapper.toDto(user.get());
    }

    @Override
    public UserResponseDTO getByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        return userMapper.toDto(user.get());
    }

    @Override
    public UserLoginResponseDTO authenticate(UserRequestDTO userRequestDTO) {
        Optional<User> user = userRepository.findByUsername(userRequestDTO.getUsername())
                .or(() -> userRepository.findByEmail(userRequestDTO.getUsername()));

        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }

        return null;
    }
}
