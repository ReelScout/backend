package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentBuilderImplementation.
 * These are pure unit tests (no Spring, no DB, no HTTP),
 * verifying that the builder correctly transfers fields into Content.
 */
class ContentBuilderImplementationTest {

    private ContentBuilderImplementation builder;

    @BeforeEach
    void setUp() {
        builder = new ContentBuilderImplementation();
    }

    @Test
    void fluentApi_returnsSameInstance() {
        // Each setter should return the same builder instance (fluent API)
        assertSame(builder, builder.id(1L));
        assertSame(builder, builder.title("Title"));
        assertSame(builder, builder.description("Description"));
        assertSame(builder, builder.contentType(new ContentType("MOVIE")));
        assertSame(builder, builder.genres(List.of()));
        assertSame(builder, builder.actors(List.of()));
        assertSame(builder, builder.directors(List.of()));
        assertSame(builder, builder.s3ImageKey("s3/key"));
        assertSame(builder, builder.trailerUrl("https://t.example"));
        assertSame(builder, builder.productionCompany(new ProductionCompany()));
    }

    @Test
    void build_transfersAllFieldsToContent() {
        // Arrange: prepare sample entities
        ContentType type = new ContentType("SERIES");
        Genre genre = new Genre();
        Actor actor = new Actor();
        Director director = new Director();
        ProductionCompany pc = new ProductionCompany();

        // Act: build the content object with all fields set
        Content content = builder
                .id(42L)
                .title("Inception")
                .description("A dream within a dream")
                .contentType(type)
                .genres(List.of(genre))
                .actors(List.of(actor))
                .directors(List.of(director))
                .s3ImageKey("img/key")
                .trailerUrl("https://t.example/trailer")
                .productionCompany(pc)
                .build();

        // Assert: all fields are transferred correctly
        assertEquals(42L, content.getId());
        assertEquals("Inception", content.getTitle());
        assertEquals("A dream within a dream", content.getDescription());
        assertEquals(type, content.getContentType());
        assertEquals(List.of(genre), content.getGenres());
        assertEquals(List.of(actor), content.getActors());
        assertEquals(List.of(director), content.getDirectors());
        assertEquals("img/key", content.getS3ImageKey());
        assertEquals("https://t.example/trailer", content.getTrailerUrl());
        assertEquals(pc, content.getProductionCompany());
    }

    @Test
    void build_withNoOptionalFields_setsNulls() {
        // Act: build a content object with only required fields
        Content content = builder
                .id(1L)
                .title("OnlyTitle")
                .contentType(new ContentType("MOVIE"))
                .build();

        // Assert: optional fields remain null
        assertNull(content.getDescription());
        assertNull(content.getGenres());
        assertNull(content.getActors());
        assertNull(content.getDirectors());
        assertNull(content.getS3ImageKey());
        assertNull(content.getTrailerUrl());
        assertNull(content.getProductionCompany());
    }

    @Test
    void builderReuse_doesNotLeakState() {
        // First build
        Content first = builder
                .id(1L)
                .title("A")
                .contentType(new ContentType("MOVIE"))
                .build();

        // Second build with different state
        Content second = builder
                .id(2L)
                .title("B")
                .contentType(new ContentType("SERIES"))
                .build();

        // Assert: each built Content preserves its own state
        assertEquals(1L, first.getId());
        assertEquals("A", first.getTitle());
        assertEquals("MOVIE", first.getContentType().getName());

        assertEquals(2L, second.getId());
        assertEquals("B", second.getTitle());
        assertEquals("SERIES", second.getContentType().getName());
    }
}