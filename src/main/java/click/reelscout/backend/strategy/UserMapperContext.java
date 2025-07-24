package click.reelscout.backend.strategy;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.User;
import lombok.Setter;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
@Setter
public class UserMapperContext {
    private UserMapper userMapper;

    public UserResponseDTO toDto(User user) {
        return (UserResponseDTO) userMapper.toDto(user);
    }

    public UserResponseDTO toDto(User user, String s3ImageKey) {
        return (UserResponseDTO) userMapper.toDto(user, s3ImageKey);
    }

    public UserBuilder toBuilder(User user) {
        return (UserBuilder) userMapper.toBuilder(user);
    }

    public User toEntity(UserRequestDTO userRequestDTO) {
        return (User) userMapper.toEntity(userRequestDTO);
    }

    public User toEntity(UserRequestDTO userRequestDTO, String s3ImageKey) {
        return (User) userMapper.toEntity(userRequestDTO, s3ImageKey);
    }
}
