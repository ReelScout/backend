package click.reelscout.backend.builder.definition;

import click.reelscout.backend.model.jpa.*;

import java.util.List;

/**
 * Builder contract for creating {@link click.reelscout.backend.model.jpa.Content} instances.
 * <p>
 * Methods follow a fluent API and return the builder for chaining.
 */
public interface ContentBuilder extends EntityBuilder<Content, ContentBuilder> {
    /** Set the content title. */
    ContentBuilder title(String title);
    /** Set the content description. */
    ContentBuilder description(String description);
    /** Set the content type. */
    ContentBuilder contentType(ContentType contentType);
    /** Set the genres list. */
    ContentBuilder genres(List<Genre> genres);
    /** Set the actors list. */
    ContentBuilder actors(List<Actor> actors);
    /** Set the directors list. */
    ContentBuilder directors(List<Director> directors);
    /** Set the S3 image key. */
    ContentBuilder s3ImageKey(String s3ImageKey);
    /** Set the trailer URL. */
    ContentBuilder trailerUrl(String trailerUrl);
    /** Set the production company. */
    ContentBuilder productionCompany(ProductionCompany productionCompany);
}
