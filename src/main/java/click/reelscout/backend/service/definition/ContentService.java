package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;

import java.util.List;

/**
 * Service definition for managing content resources.
 * <p>
 * Provides operations to create, update, retrieve and delete content, as well
 * as utility methods for available content types and genres.
 */
public interface ContentService {
    /**
     * Create a new content entry for the given production company.
     *
     * @param authenticatedProduction the production company performing the operation
     * @param contentRequestDTO       the DTO containing content data to create
     * @return the created content as {@link ContentResponseDTO}
     */
    ContentResponseDTO create(ProductionCompany authenticatedProduction, ContentRequestDTO contentRequestDTO);

    /**
     * Update an existing content entry.
     *
     * @param authenticatedProduction the production company performing the operation
     * @param id                      the id of the content to update
     * @param contentRequestDTO       the DTO containing updated content data
     * @return the updated content as {@link ContentResponseDTO}
     */
    ContentResponseDTO update(ProductionCompany authenticatedProduction, Long id, ContentRequestDTO contentRequestDTO);

    /**
     * Retrieve all available content.
     *
     * @return list of {@link ContentResponseDTO} representing all content
     */
    List<ContentResponseDTO> getAll();

    /**
     * Retrieve all content belonging to a specific production company.
     *
     * @param authenticatedProduction the production company whose content should be returned
     * @return list of {@link ContentResponseDTO} for the given production
     */
    List<ContentResponseDTO> getByProductionCompany(ProductionCompany authenticatedProduction);

    /**
     * Delete a content entry.
     *
     * @param authenticatedProduction the production company performing the delete
     * @param id                      the id of the content to delete
     * @return a {@link CustomResponseDTO} describing the result
     */
    CustomResponseDTO delete(ProductionCompany authenticatedProduction, Long id);

    /**
     * Get the list of supported content types (e.g. movie, series).
     *
     * @return list of content type names
     */
    List<String> getContentTypes();

    /**
     * Get the list of supported genres.
     *
     * @return list of genre names
     */
    List<String> getGenres();
}
