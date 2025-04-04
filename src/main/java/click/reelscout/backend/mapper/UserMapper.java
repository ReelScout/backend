package click.reelscout.backend.mapper;

import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.User;

public class UserMapper {
    public static UserResponseDTO toDto(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getPassword());
    }
}
