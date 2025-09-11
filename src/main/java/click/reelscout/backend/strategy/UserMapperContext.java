package click.reelscout.backend.strategy;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Context class for UserMapper strategy.
 * This class uses generics to handle different types of User, UserBuilder, UserRequestDTO, UserResponseDTO, and UserMapper.
 *
 * @param <U> Type of User entity
 * @param <B> Type of UserBuilder
 * @param <R> Type of UserRequestDTO
 * @param <S> Type of UserResponseDTO
 * @param <M> Type of UserMapper
 */
@Component
@Setter
public class UserMapperContext <U extends User, B extends UserBuilder<U, B>, R extends UserRequestDTO, S extends UserResponseDTO, M extends UserMapper<U, R, S, B>> {
    private M userMapper;

    /** Convert User entity to UserResponseDTO.
     *
     * @param user        User entity
     * @param s3ImageKey S3 image key for the user's profile picture
     * @return UserResponseDTO
     */
    public S toDto(U user, String s3ImageKey) {
        return userMapper.toDto(user, s3ImageKey);
    }

    /** Convert User entity to UserBuilder.
     *
     * @param user User entity
     * @return UserBuilder
     */
    public B toBuilder(U user) {
        return userMapper.toBuilder(user);
    }

    /** Convert UserRequestDTO to User entity.
     *
     * @param userRequestDTO UserRequestDTO
     * @param s3ImageKey     S3 image key for the user's profile picture
     * @return User entity
     */
    public U toEntity(R userRequestDTO, String s3ImageKey) {
        return userMapper.toEntity(userRequestDTO, s3ImageKey);
    }

    /** Convert User entity to UserDoc for Elasticsearch.
     *
     * @param user User entity
     * @return UserDoc
     */
    public UserDoc toUserDoc(U user) {
        return userMapper.toDoc(user);
    }
}
