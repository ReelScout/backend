package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.model.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class ContentBuilderImplementation implements ContentBuilder {
    private Long id;
    private String title;
    private String description;
    private ContentType contentType;
    private List<Genre> genres;
    private List<Actor> actors;
    private List<Director> directors;
    private String s3ImageKey;
    private String trailerUrl;
    private ProductionCompany productionCompany;

    @Override
    public ContentBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public ContentBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ContentBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ContentBuilder contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public ContentBuilder genres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    @Override
    public ContentBuilder actors(List<Actor> actors) {
        this.actors = actors;
        return this;
    }

    @Override
    public ContentBuilder directors(List<Director> directors) {
        this.directors = directors;
        return this;
    }

    @Override
    public ContentBuilder s3ImageKey(String s3ImageKey) {
        this.s3ImageKey = s3ImageKey;
        return this;
    }

    @Override
    public ContentBuilder trailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
        return this;
    }

    @Override
    public ContentBuilder productionCompany(ProductionCompany productionCompany) {
        this.productionCompany = productionCompany;
        return this;
    }

    @Override
    public Content build() {
        return new Content(this);
    }
}
