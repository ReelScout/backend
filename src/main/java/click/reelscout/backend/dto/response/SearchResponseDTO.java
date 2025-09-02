package click.reelscout.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchResponseDTO<S extends  UserResponseDTO> {
    private List<S> users;

    private List<ContentResponseDTO> contents;
}
