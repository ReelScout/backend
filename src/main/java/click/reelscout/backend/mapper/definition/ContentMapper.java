package click.reelscout.backend.mapper.definition;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ProductionCompany;

public interface ContentMapper {
    ContentResponseDTO toDto(Content content, ProductionCompany productionCompany, String base64Image);

    ContentBuilder toBuilder(Content content);

    Content toEntity(ContentRequestDTO contentRequestDTO, ProductionCompany productionCompany, String s3ImageKey);
}
