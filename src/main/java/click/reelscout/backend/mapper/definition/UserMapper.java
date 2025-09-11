package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;

/**
 * Generic mapper interface for converting between user-related entities, DTOs, builders, and documents.
 *
 * @param <U> the type of user entity
 * @param <R> the type of user request DTO
 * @param <S> the type of user response DTO
 * @param <B> the type of user builder
 */
public interface UserMapper<U extends User, R extends UserRequestDTO, S extends UserResponseDTO, B extends UserBuilder<U, B>> {
    /**
     * Converts a user entity to its corresponding DTO.
     *
     * @param user the user entity to convert
     * @param base64Image the base64-encoded image associated with the user
     * @return the corresponding response DTO
     */
    S toDto(U user, String base64Image);

    /**
     * Converts a user entity to its builder representation.
     *
     * @param user the user entity to convert
     * @return the corresponding builder
     */
    B toBuilder(U user);

    /**
     * Creates a user entity from the given request DTO and image key.
     *
     * @param dto the request DTO containing user data
     * @param s3ImageKey the S3 key for the user's image
     * @return the created user entity
     */
    U toEntity(R dto, String s3ImageKey);

    /**
     * Converts a user entity to its corresponding document representation.
     *
     * @param user the user entity to convert
     * @return the corresponding document
     */
    UserDoc toDoc(U user);
}