package click.reelscout.backend.strategy;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public class UserMapperContext <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>> {
    private M userMapper;

    public S toDto(U user, String s3ImageKey) {
        return userMapper.toDto(user, s3ImageKey);
    }

    public B toBuilder(U user) {
        return userMapper.toBuilder(user);
    }

    public U toEntity(R userRequestDTO, String s3ImageKey) {
        return userMapper.toEntity(userRequestDTO, s3ImageKey);
    }

    public UserDoc toUserDoc(U user) {
        return userMapper.toDoc(user);
    }

}
