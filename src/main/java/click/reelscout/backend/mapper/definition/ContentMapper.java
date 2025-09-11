package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.model.elasticsearch.ContentDoc;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ProductionCompany;

/**
 * Mapper interface for converting between {@link Content} entities, DTOs, builders, and documents.
 */
public interface ContentMapper {
    /**
     * Converts a {@link Content} entity to its corresponding DTO.
     *
     * @param content the content entity to convert
     * @param base64Image the base64-encoded image associated with the content
     * @return the corresponding {@link ContentResponseDTO}
     */
    ContentResponseDTO toDto(Content content, String base64Image);

    /**
     * Converts a {@link Content} entity to its builder representation.
     *
     * @param content the content entity to convert
     * @return the corresponding {@link ContentBuilder}
     */
    ContentBuilder toBuilder(Content content);

    /**
     * Creates a {@link Content} entity from the given request DTO, production company, and image key.
     *
     * @param contentRequestDTO the DTO containing content data
     * @param productionCompany the production company associated with the content
     * @param s3ImageKey the S3 key for the content's image
     * @return the created {@link Content} entity
     */
    Content toEntity(ContentRequestDTO contentRequestDTO, ProductionCompany productionCompany, String s3ImageKey);

    /**
     * Converts a {@link Content} entity to its corresponding document representation.
     *
     * @param content the content entity to convert
     * @return the corresponding {@link ContentDoc}
     */
    ContentDoc toDoc(Content content);
}
