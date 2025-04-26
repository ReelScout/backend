package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapperImplementation implements UserMapper {
    private final UserBuilder userBuilder;

    @Override
    public UserResponseDTO toDto(User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    public UserBuilder toBuilder(User user) {
        return userBuilder
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole());
    }

    @Override
    public User toEntity(UserRequestDTO userRequestDTO) {
        return userBuilder
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .birthDate(userRequestDTO.getBirthDate())
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                //TODO: implement roles if needed
                .build();
    }
}
