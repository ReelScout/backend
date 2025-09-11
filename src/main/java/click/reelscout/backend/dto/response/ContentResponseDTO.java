package click.reelscout.backend.dto.response;

import click.reelscout.backend.model.jpa.Actor;
import click.reelscout.backend.model.jpa.ContentType;
import click.reelscout.backend.model.jpa.Director;
import click.reelscout.backend.model.jpa.Genre;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Content response.
 * Extends EntityResponseDTO to include common entity fields.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class ContentResponseDTO extends EntityResponseDTO {
    private String title;
    private String description;
    private ContentType contentType;
    private List<Genre> genres;
    private List<Actor> actors;
    private List<Director> directors;
    private String base64Image;
    private String trailerUrl;
    private Long productionCompanyId;
    private String productionCompanyName;

    public ContentResponseDTO(
            Long id,
            String title,
            String description,
            ContentType contentType,
            List<Genre> genres,
            List<Actor> actors,
            List<Director> directors,
            String base64Image,
            String trailerUrl,
            Long productionCompanyId,
            String productionCompanyName
    ) {
        super(id);
        this.actors = actors;
        this.contentType = contentType;
        this.genres = genres;
        this.description = description;
        this.directors = directors;
        this.title = title;
        this.base64Image = base64Image;
        this.trailerUrl = trailerUrl;
        this.productionCompanyId = productionCompanyId;
        this.productionCompanyName = productionCompanyName;
    }
}
