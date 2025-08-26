package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.*;

import java.util.List;

public interface ContentBuilder extends EntityBuilder<Content, ContentBuilder> {
    ContentBuilder title(String title);
    ContentBuilder description(String description);
    ContentBuilder contentType(ContentType contentType);
    ContentBuilder actors(List<Actor> actors);
    ContentBuilder directors(List<Director> directors);
    ContentBuilder s3ImageKey(String s3ImageKey);
    ContentBuilder trailerUrl(String trailerUrl);
    ContentBuilder productionCompany(ProductionCompany productionCompany);
}
