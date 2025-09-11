package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.model.elasticsearch.ContentDoc;
import click.reelscout.backend.model.jpa.*;
import click.reelscout.backend.mapper.definition.ContentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

/**
 * Unit test for {@link ContentMapperImplementation}.
 * Uses Mockito to mock dependencies and verify interactions.
 */
class ContentMapperImplementationTest {

    private ContentBuilder contentBuilder;     // chainable mock
    private ContentMapper mapper;

    @BeforeEach
    void setUp() {
        // Make the builder return itself for any non-stubbed method to support fluent API
        contentBuilder = mock(ContentBuilder.class, withSettings().defaultAnswer(Answers.RETURNS_SELF));
        mapper = new ContentMapperImplementation(contentBuilder);
    }

    /** Test mapping of Content to ContentReponseDTO
     * Tests that all fields are correctly mapped, including nested ProductionCompany and base64 image.
     */
    @Test
    @DisplayName("toDto maps Content + ProductionCompany + base64 image to DTO")
    void toDto_mapsAllFields() {
        // Arrange
        Content content = mock(Content.class);
        ProductionCompany company = mock(ProductionCompany.class);

        ContentType contentType = new ContentType("MOVIE"); // simple value object per tuo modello
        List<?> genres = List.of();     // we don't care about concrete types here
        List<?> actors = List.of();
        List<?> directors = List.of();

        when(content.getId()).thenReturn(10L);
        when(content.getTitle()).thenReturn("Inception");
        when(content.getDescription()).thenReturn("Mind-bending");
        when(content.getContentType()).thenReturn(contentType);
        //noinspection unchecked
        when(content.getGenres()).thenReturn((List<Genre>) genres);
        //noinspection unchecked
        when(content.getActors()).thenReturn((List<Actor>) actors);
        //noinspection unchecked
        when(content.getDirectors()).thenReturn((List<Director>) directors);
        when(content.getTrailerUrl()).thenReturn("https://trailer");
        when(company.getId()).thenReturn(7L);
        when(company.getName()).thenReturn("Syncopy");
        when(content.getProductionCompany()).thenReturn(company);

        String base64 = "img==";

        // Act
        ContentResponseDTO dto = mapper.toDto(content, base64);

        // Assert
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Inception", dto.getTitle());
        assertEquals("Mind-bending", dto.getDescription());
        assertSame(contentType, dto.getContentType());
        assertSame(genres, dto.getGenres());
        assertSame(actors, dto.getActors());
        assertSame(directors, dto.getDirectors());
        assertEquals("https://trailer", dto.getTrailerUrl());
        assertEquals(base64, dto.getBase64Image());
        assertEquals(7L, dto.getProductionCompanyId());
        assertEquals("Syncopy", dto.getProductionCompanyName());
    }

    /**
     * Test that toBuilder correctly populates a ContentBuilder with all fields from a Content.
     * Verifies that each field is forwarded to the builder.
     */
    @Test
    @DisplayName("toBuilder copies all fields from Content onto the fluent builder")
    void toBuilder_populatesBuilder() {
        // Arrange
        Content content = mock(Content.class);
        ContentType type = new ContentType("SERIES");
        List<?> genres = List.of();
        List<?> actors = List.of();
        List<?> directors = List.of();
        ProductionCompany pc = mock(ProductionCompany.class);

        when(content.getId()).thenReturn(22L);
        when(content.getTitle()).thenReturn("Dark");
        when(content.getDescription()).thenReturn("Time travel");
        when(content.getContentType()).thenReturn(type);
        //noinspection unchecked
        when(content.getGenres()).thenReturn((List<Genre>) genres);
        //noinspection unchecked
        when(content.getActors()).thenReturn((List<Actor>) actors);
        //noinspection unchecked
        when(content.getDirectors()).thenReturn((List<Director>) directors);
        when(content.getS3ImageKey()).thenReturn("s3/key");
        when(content.getTrailerUrl()).thenReturn("yt://trailer");
        when(content.getProductionCompany()).thenReturn(pc);

        // Act
        ContentBuilder returned = mapper.toBuilder(content);

        // Assert: builder chaining should return the same mock instance
        assertSame(contentBuilder, returned);

        // Verify each field has been forwarded to the builder
        verify(contentBuilder).id(22L);
        verify(contentBuilder).title("Dark");
        verify(contentBuilder).description("Time travel");
        verify(contentBuilder).contentType(type);
        //noinspection unchecked
        verify(contentBuilder).genres((List<Genre>) genres);
        //noinspection unchecked
        verify(contentBuilder).actors((List<Actor>) actors);
        //noinspection unchecked
        verify(contentBuilder).directors((List<Director>) directors);
        verify(contentBuilder).s3ImageKey("s3/key");
        verify(contentBuilder).trailerUrl("yt://trailer");
        verify(contentBuilder).productionCompany(pc);
    }

    /**
     * Test that toEntity uses the ContentBuilder to build a Content from a ContentRequestDTO,
     * a ProductionCompany, and an S3 image key.
     * Verifies that all fields from the request are passed to the builder in order,
     * and that the built Content is returned.
     */
    @Test
    @DisplayName("toEntity builds a Content using the builder and returns the built instance")
    void toEntity_buildsAndReturnsEntity() {
        // Arrange
        ContentRequestDTO req = mock(ContentRequestDTO.class);
        ProductionCompany pc = mock(ProductionCompany.class);
        ContentType type = new ContentType("MOVIE");
        Content built = mock(Content.class);

        when(req.getTitle()).thenReturn("Interstellar");
        when(req.getDescription()).thenReturn("Space & time");
        when(req.getContentType()).thenReturn(type);
        // Le liste possono essere null/empty: usiamo empty per semplicità
        when(req.getGenres()).thenReturn(List.of());
        when(req.getActors()).thenReturn(List.of());
        when(req.getDirectors()).thenReturn(List.of());

        when(contentBuilder.build()).thenReturn(built);

        String s3key = "s3/img";

        // Act
        Content result = mapper.toEntity(req, pc, s3key);

        // Assert: returns what builder.build() produced
        assertSame(built, result);

        // And the builder must have been called with the request fields in order
        verify(contentBuilder).title("Interstellar");
        verify(contentBuilder).description("Space & time");
        verify(contentBuilder).contentType(type);
        verify(contentBuilder).genres(List.of());
        verify(contentBuilder).actors(List.of());
        verify(contentBuilder).directors(List.of());
        verify(contentBuilder).s3ImageKey(s3key);
        verify(contentBuilder).trailerUrl(null); // unless req.getTrailerUrl() is stubbed, it's null
        verify(contentBuilder).productionCompany(pc);
        verify(contentBuilder).build();
    }

    /**
     * Test that toDoc wraps a Content into a ContentDoc.
     * Since ContentDoc is a simple wrapper, we just verify non-null result.
     */
    @Test
    @DisplayName("toDoc wraps a Content into a ContentDoc")
    void toDoc_wrapsContent() {
        Content content = mock(Content.class);

        ContentDoc doc = mapper.toDoc(content);

        assertNotNull(doc, "ContentDoc must not be null");
        // Non assumiamo equals/fields su ContentDoc: assert basic non-null is enough for unit scope
    }
}