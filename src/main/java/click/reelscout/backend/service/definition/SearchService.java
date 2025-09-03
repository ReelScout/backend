package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;

public interface SearchService<S extends UserResponseDTO> {
    SearchResponseDTO<S> search(String query);
}
