package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO for search response containing lists of users and contents.
 *
 * @param <S> the type of UserResponseDTO
 */
@AllArgsConstructor
@Data
public class SearchResponseDTO<S extends  UserResponseDTO> {
    private List<S> users;

    private List<ContentResponseDTO> contents;
}
