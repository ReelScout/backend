package click.reelscout.backend.service.implementation;

import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import click.reelscout.backend.repository.UserRepository;
import click.reelscout.backend.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserResponseDTO getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElse(null);
    }
}
