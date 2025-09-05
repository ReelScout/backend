package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.service.definition.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for SearchController.
 * - No Spring context or MockMvc.
 * - We instantiate the controller directly with a mocked SearchService.
 * - The @RequestMapping("${api.paths.search}") placeholder is irrelevant here.
 */
@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService<UserResponseDTO> searchService;

    private SearchController<UserResponseDTO> controller;

    @BeforeEach
    void setUp() {
        controller = new SearchController<>(searchService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void search_returnsOkAndBodyFromService() {
        // Arrange
        String query = "test-user";
        SearchResponseDTO<UserResponseDTO> expected = mock(SearchResponseDTO.class);
        when(searchService.search(query)).thenReturn(expected);

        // Act
        ResponseEntity<SearchResponseDTO<UserResponseDTO>> res = controller.search(query);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode(), "Response must have HTTP 200 status");
        assertSame(expected, res.getBody(), "Controller must return the service result as-is");
        verify(searchService).search(query);
        verifyNoMoreInteractions(searchService);
    }

    @Test
    void search_returnsOkEvenWhenServiceReturnsNull() {
        // Arrange
        String query = "non-existing";
        when(searchService.search(query)).thenReturn(null);

        // Act
        ResponseEntity<SearchResponseDTO<UserResponseDTO>> res = controller.search(query);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNull(res.getBody(), "Controller should propagate null result if service returns null");
        verify(searchService).search(query);
        verifyNoMoreInteractions(searchService);
    }
}