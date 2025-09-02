package click.reelscout.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SearchResponseDTO<S extends  UserResponseDTO> {
    private List<S> users;

    private List<ContentResponseDTO> contents;
}
