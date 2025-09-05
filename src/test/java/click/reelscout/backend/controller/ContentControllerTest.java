package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.service.definition.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for ContentController.
 * - No Spring context or MockMvc is used.
 * - We instantiate the controller directly and mock ContentService.
 * - @RequestMapping("${api.paths.content}") resolution is irrelevant here because
 *   we call controller methods directly.
 */
@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    @Mock
    private ContentService contentService;

    private ContentController controller;

    @BeforeEach
    void setUp() {
        controller = new ContentController(contentService);
    }

    @Test
    void all_returnsOkWithBodyFromService() {
        // Arrange
        List<ContentResponseDTO> expected = List.of(
                new ContentResponseDTO(), new ContentResponseDTO()
        );
        when(contentService.getAll()).thenReturn(expected);

        // Act
        ResponseEntity<List<ContentResponseDTO>> res = controller.all();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody(), "Controller must return service result as-is");
        verify(contentService).getAll();
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void contentTypes_returnsOkWithBodyFromService() {
        // Arrange
        List<String> expected = List.of("MOVIE", "SERIES");
        when(contentService.getContentTypes()).thenReturn(expected);

        // Act
        ResponseEntity<List<String>> res = controller.contentTypes();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).getContentTypes();
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void genres_returnsOkWithBodyFromService() {
        // Arrange
        List<String> expected = List.of("Action", "Drama");
        when(contentService.getGenres()).thenReturn(expected);

        // Act
        ResponseEntity<List<String>> res = controller.genres();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).getGenres();
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void endpoints_returnOkEvenWhenServiceReturnsEmptyLists() {
        // Arrange
        when(contentService.getAll()).thenReturn(List.of());
        when(contentService.getContentTypes()).thenReturn(List.of());
        when(contentService.getGenres()).thenReturn(List.of());

        // Act: call each endpoint ONCE and reuse the response objects
        var resAll = controller.all();
        var resTypes = controller.contentTypes();
        var resGenres = controller.genres();

        // Assert: status + bodies
        assertEquals(HttpStatus.OK, resAll.getStatusCode());
        assertNotNull(resAll.getBody());
        assertTrue(resAll.getBody().isEmpty());

        assertEquals(HttpStatus.OK, resTypes.getStatusCode());
        assertNotNull(resTypes.getBody());
        assertTrue(resTypes.getBody().isEmpty());

        assertEquals(HttpStatus.OK, resGenres.getStatusCode());
        assertNotNull(resGenres.getBody());
        assertTrue(resGenres.getBody().isEmpty());

        // Verify exactly one interaction per service method
        verify(contentService).getAll();
        verify(contentService).getContentTypes();
        verify(contentService).getGenres();
        verifyNoMoreInteractions(contentService);
    }
}