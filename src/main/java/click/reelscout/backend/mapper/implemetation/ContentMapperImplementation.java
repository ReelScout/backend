package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.model.elasticsearch.ContentDoc;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ProductionCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentMapperImplementation implements ContentMapper {
    private final ContentBuilder contentBuilder;

    @Override
    public ContentResponseDTO toDto(Content content, ProductionCompany productionCompany, String base64Image) {
        return new ContentResponseDTO(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getContentType(),
                content.getGenres(),
                content.getActors(),
                content.getDirectors(),
                base64Image,
                content.getTrailerUrl(),
                productionCompany.getId(),
                productionCompany.getName()
        );
    }

    @Override
    public ContentBuilder toBuilder(Content content) {
        return contentBuilder
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getContentType())
                .genres(content.getGenres())
                .actors(content.getActors())
                .directors(content.getDirectors())
                .s3ImageKey(content.getS3ImageKey())
                .trailerUrl(content.getTrailerUrl())
                .productionCompany(content.getProductionCompany());
    }

    @Override
    public Content toEntity(ContentRequestDTO contentRequestDTO, ProductionCompany productionCompany, String s3ImageKey) {
        return contentBuilder
                .title(contentRequestDTO.getTitle())
                .description(contentRequestDTO.getDescription())
                .contentType(contentRequestDTO.getContentType())
                .genres(contentRequestDTO.getGenres())
                .actors(contentRequestDTO.getActors())
                .directors(contentRequestDTO.getDirectors())
                .s3ImageKey(s3ImageKey)
                .trailerUrl(contentRequestDTO.getTrailerUrl())
                .productionCompany(productionCompany)
                .build();
    }

    @Override
    public ContentDoc toDoc(Content content) {
        return new ContentDoc(content);
    }
}
