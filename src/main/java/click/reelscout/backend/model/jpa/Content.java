package click.reelscout.backend.model.jpa;

import click.reelscout.backend.builder.implementation.ContentBuilderImplementation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity representing content such as movies or TV shows.
 */
@NoArgsConstructor
@Entity
@Getter
public class Content implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ContentType contentType;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Genre> genres;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Actor> actors;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Director> directors;

    String s3ImageKey;

    String trailerUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    private ProductionCompany productionCompany;

    public Content(ContentBuilderImplementation contentBuilder) {
        this.id = contentBuilder.getId();
        this.title = contentBuilder.getTitle();
        this.description = contentBuilder.getDescription();
        this.contentType = contentBuilder.getContentType();
        this.genres = contentBuilder.getGenres();
        this.actors = contentBuilder.getActors();
        this.directors = contentBuilder.getDirectors();
        this.s3ImageKey = contentBuilder.getS3ImageKey();
        this.trailerUrl = contentBuilder.getTrailerUrl();
        this.productionCompany = contentBuilder.getProductionCompany();
    }
}
