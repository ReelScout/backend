package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.UserBuilder;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.elasticsearch.UserDoc;
import click.reelscout.backend.model.jpa.User;

public interface UserMapper<U extends User, R extends UserRequestDTO, S extends UserResponseDTO, B extends UserBuilder<U, B>> {
    S toDto(U user, String base64Image);

    B toBuilder(U user);

    U toEntity(R dto, String s3ImageKey);

    UserDoc toDoc(U user);
}