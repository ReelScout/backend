package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;

import java.util.List;

/**
 * Service definition for searching content and members.
 */
public interface SearchService<S extends UserResponseDTO> {
    /**
     * Perform a general search across content and members.
     *
     * @param query the search query
     * @return a {@link SearchResponseDTO} containing results
     */
    SearchResponseDTO<S> search(String query);

    /**
     * Search members by query.
     *
     * @param query the search query
     * @return list of user DTOs matching the query
     */
    List<S> searchMembers(String query);
}
