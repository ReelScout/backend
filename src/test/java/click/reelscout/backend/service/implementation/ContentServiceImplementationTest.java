package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.ContentBuilder;
import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.model.elasticsearch.ContentDoc;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ContentType;
import click.reelscout.backend.model.jpa.Genre;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.repository.elasticsearch.ContentElasticRepository;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.ContentTypeRepository;
import click.reelscout.backend.repository.jpa.GenreRepository;
import click.reelscout.backend.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ContentServiceImplementation.
 * - No Spring context.
 * - All collaborators are mocked.
 * - We assert returned values and key interACTIONs/branches.
 */
@ExtendWith(MockitoExtension.class)
class ContentServiceImplementationTest {

    @Mock private ContentRepository contentRepository;
    @Mock private ContentElasticRepository contentElasticRepository;
    @Mock private ContentTypeRepository contentTypeRepository;
    @Mock private GenreRepository genreRepository;
    @Mock private S3Service s3Service;
    @Mock private ContentMapper contentMapper;

    @InjectMocks
    private ContentServiceImplementation service;

    // --------- helpers ---------

    /** Creates a simple DTO with minimal non-null fields for create/update paths. */
    private ContentRequestDTO mkDto(String base64Image) {
        ContentRequestDTO dto = new ContentRequestDTO();
        dto.setTitle("t");
        dto.setDescription("d");
        ContentType ct = new ContentType("MOVIE"); // your entity has a (String) ctor
        dto.setContentType(ct);
        dto.setGenres(List.of(new Genre("ACTION"), new Genre("DRAMA")));
        dto.setActors(List.of());     // leave empty if not relevant to the test
        dto.setDirectors(List.of());  // idem
        dto.setTrailerUrl("url");
        dto.setBase64Image(base64Image);
        return dto;
    }

    /** Mocks the genre save-if-missing pipeline to return exactly the input list. */
    private void stubGenresRoundTrip(List<Genre> input) {
        when(genreRepository.findAllByNameIgnoreCaseIn(anyList())).thenReturn(List.of())  // first call: existing = []
                .thenReturn(input);                  // second call: return saved list
        when(genreRepository.saveAll(anyList())).thenAnswer(AdditionalAnswers.returnsFirstArg());
    }

    // --------- create() ---------

    @Test
    @DisplayName("create(): happy path with base64 image -> persists, indexes, uploads and returns DTO")
    void create_withImage_success() {
        var producer = mock(ProductionCompany.class);
        var dto = mkDto("base64-img");

        // content type not existing -> saved
        when(contentTypeRepository.existsByNameIgnoreCase("MOVIE")).thenReturn(false);

        // genres flow
        stubGenresRoundTrip(dto.getGenres());

        // mapper chain: toEntity -> toBuilder -> genres(...).build()
        Content entityFromDto = mock(Content.class);
        ContentBuilder builder = mock(ContentBuilder.class, RETURNS_SELF);
        Content contentToSave = mock(Content.class);
        when(contentMapper.toEntity(any(ContentRequestDTO.class), any(ProductionCompany.class), any())).thenReturn(entityFromDto);
        when(contentMapper.toBuilder(entityFromDto)).thenReturn(builder);
        when(builder.genres(anyList())).thenReturn(builder);
        when(builder.build()).thenReturn(contentToSave);

        // repository & indexing
        Content saved = mock(Content.class);
        when(contentRepository.save(contentToSave)).thenReturn(saved);
        ContentDoc doc = mock(ContentDoc.class);
        when(contentMapper.toDoc(saved)).thenReturn(doc);

        // DTO response
        ContentResponseDTO response = new ContentResponseDTO();
        when(contentMapper.toDto(any(Content.class), anyString())).thenReturn(response);

        // act
        ContentResponseDTO res = service.create(producer, dto);

        // assert
        assertSame(response, res);
        verify(contentTypeRepository).save(dto.getContentType());
        verify(contentElasticRepository).save(doc);
        verify(s3Service).uploadFile(startsWith("content/"), eq("base64-img"));
    }

    @Test
    @DisplayName("create(): wraps any exception into EntityCreateException")
    void create_wrapsInEntityCreateException() {
        var producer = mock(ProductionCompany.class);
        var dto = mkDto("img");

        when(contentTypeRepository.existsByNameIgnoreCase("MOVIE")).thenReturn(true);
        stubGenresRoundTrip(dto.getGenres());

        // make mapper throw inside try block
        when(contentMapper.toEntity(any(), any(), any())).thenThrow(new RuntimeException("boom"));

        assertThrows(EntityCreateException.class, () -> service.create(producer, dto));
        verify(contentRepository, never()).save(any());
        verify(contentElasticRepository, never()).save(any());
    }

    // --------- update() ---------

    @Test
    @DisplayName("update(): happy path with new image -> generates new key, persists, indexes, uploads and returns DTO")
    void update_withNewImage_success() {
        var producer = mock(ProductionCompany.class);
        var dto = mkDto("new-base64");
        Long id = 42L;

        // existing content
        Content existing = mock(Content.class);
        when(existing.getS3ImageKey()).thenReturn("old/key");
        when(existing.getProductionCompany()).thenReturn(producer);
        when(contentRepository.findById(id)).thenReturn(Optional.of(existing));

        // content type exists (or is saved)
        when(contentTypeRepository.existsByNameIgnoreCase("MOVIE")).thenReturn(true);

        // genres
        stubGenresRoundTrip(dto.getGenres());

        // builder chain from existing
        ContentBuilder builder = mock(ContentBuilder.class, RETURNS_SELF);
        doReturn(builder).when(contentMapper).toBuilder(any());        Content updated = mock(Content.class);
        when(builder.build()).thenReturn(updated);

        // repo & indexing
        Content saved = mock(Content.class);
        when(contentRepository.save(updated)).thenReturn(saved);
        ContentDoc doc = mock(ContentDoc.class);
        when(contentMapper.toDoc(saved)).thenReturn(doc);

        // response
        ContentResponseDTO response = new ContentResponseDTO();
        when(contentMapper.toDto(any(Content.class), anyString())).thenReturn(response);

        // act
        ContentResponseDTO res = service.update(producer, id, dto);

        // assert
        assertSame(response, res);
        verify(s3Service).uploadFile("old/key", "new-base64");
        verify(contentElasticRepository).save(doc);
    }

    @Test
    @DisplayName("update(): throws EntityNotFoundException when id is missing")
    void update_notFound_throws() {
        when(contentRepository.findById(99L)).thenReturn(Optional.empty());

        ProductionCompany pc = mock(ProductionCompany.class);
        ContentRequestDTO dto = mkDto("x");

        assertThrows(EntityNotFoundException.class,
                () -> service.update(pc, 99L, dto));
    }

    @Test
    @DisplayName("update(): wraps any exception into EntityUpdateException")
    void update_wrapsInEntityUpdateException() {
        var producer = mock(ProductionCompany.class);
        var dto = mkDto("img");
        Long id = 1L;

        Content existing = mock(Content.class);
        when(contentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(existing.getS3ImageKey()).thenReturn(null);
        when(existing.getProductionCompany()).thenReturn(producer);

        when(contentTypeRepository.existsByNameIgnoreCase("MOVIE")).thenReturn(true);
        stubGenresRoundTrip(dto.getGenres());

        ContentBuilder builder = mock(ContentBuilder.class, RETURNS_SELF);
        doReturn(builder).when(contentMapper).toBuilder(any());        Content updated = mock(Content.class);
        when(builder.build()).thenReturn(updated);
        when(contentRepository.save(updated)).thenThrow(new RuntimeException("explode"));

        assertThrows(EntityUpdateException.class, () -> service.update(producer, id, dto));
        // save() is invoked and throws inside the try-block, so it must have been called
        verify(contentRepository).save(updated);
        // After the failure, no further operations should happen
        verify(contentElasticRepository, never()).save(any());
    }

    // --------- reads ---------

    @Test
    @DisplayName("getAll(): maps each entity to DTO with image loaded from S3")
    void getAll_mapsToDto() {
        Content c1 = mock(Content.class);
        Content c2 = mock(Content.class);

        when(c1.getS3ImageKey()).thenReturn("k1");
        when(c2.getS3ImageKey()).thenReturn("k2");

        when(contentRepository.findAll()).thenReturn(List.of(c1, c2));
        when(s3Service.getFile("k1")).thenReturn("img1");
        when(s3Service.getFile("k2")).thenReturn("img2");

        ContentResponseDTO d1 = new ContentResponseDTO();
        ContentResponseDTO d2 = new ContentResponseDTO();
        when(contentMapper.toDto(c1, "img1")).thenReturn(d1);
        when(contentMapper.toDto(c2, "img2")).thenReturn(d2);

        List<ContentResponseDTO> result = service.getAll();

        assertEquals(2, result.size());
        assertSame(d1, result.get(0));
        assertSame(d2, result.get(1));
    }

    @Test
    @DisplayName("getContentTypes(): returns names from repository")
    void getContentTypes_returnsNames() {
        when(contentTypeRepository.findAll()).thenReturn(List.of(new ContentType("MOVIE"), new ContentType("SERIES")));
        assertEquals(List.of("MOVIE", "SERIES"), service.getContentTypes());
    }

    @Test
    @DisplayName("getGenres(): returns names from repository")
    void getGenres_returnsNames() {
        when(genreRepository.findAll()).thenReturn(List.of(new Genre("ACTION"), new Genre("DRAMA")));
        assertEquals(List.of("ACTION", "DRAMA"), service.getGenres());
    }

    @Test
    @DisplayName("getByProductionCompany(): maps to DTO with S3 file")
    void getByProductionCompany_mapsToDto() {
        var pc = mock(ProductionCompany.class);
        Content c = mock(Content.class);
        when(contentRepository.findAllByProductionCompany(pc)).thenReturn(List.of(c));
        when(c.getS3ImageKey()).thenReturn("k");
        when(s3Service.getFile("k")).thenReturn("img");
        ContentResponseDTO dto = new ContentResponseDTO();
        when(contentMapper.toDto(c, "img")).thenReturn(dto);

        List<ContentResponseDTO> result = service.getByProductionCompany(pc);

        assertEquals(1, result.size());
        assertSame(dto, result.getFirst());
    }

    // --------- delete() ---------

    @Test
    @DisplayName("delete(): throws when the authenticated production does not own the content")
    void delete_unauthorized_throws() {
        var pc = mock(ProductionCompany.class);
        when(pc.getId()).thenReturn(1L);

        var owner = mock(ProductionCompany.class);
        when(owner.getId()).thenReturn(2L);

        Content c = mock(Content.class);
        when(c.getProductionCompany()).thenReturn(owner);
        when(contentRepository.findById(10L)).thenReturn(Optional.of(c));

        assertThrows(EntityDeleteException.class, () -> service.delete(pc, 10L));
        verify(contentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete(): happy path -> deletes entity and S3 object (if present)")
    void delete_success_deletesAndRemovesS3() {
        var pc = mock(ProductionCompany.class);
        when(pc.getId()).thenReturn(7L);

        Content c = mock(Content.class);
        when(c.getProductionCompany()).thenReturn(pc);
        when(c.getS3ImageKey()).thenReturn("k");
        when(contentRepository.findById(5L)).thenReturn(Optional.of(c));

        CustomResponseDTO res = service.delete(pc, 5L);

        assertEquals("Content deleted successfully", res.getMessage());
        verify(contentRepository).delete(c);
        verify(s3Service).deleteFile("k");
    }

    @Test
    @DisplayName("delete(): wraps repository errors into EntityDeleteException")
    void delete_wrapsInEntityDeleteException() {
        var pc = mock(ProductionCompany.class);
        when(pc.getId()).thenReturn(7L);

        Content c = mock(Content.class);
        when(c.getProductionCompany()).thenReturn(pc);
        when(contentRepository.findById(5L)).thenReturn(Optional.of(c));

        doThrow(new RuntimeException("db")).when(contentRepository).delete(c);

        assertThrows(EntityDeleteException.class, () -> service.delete(pc, 5L));
    }
}