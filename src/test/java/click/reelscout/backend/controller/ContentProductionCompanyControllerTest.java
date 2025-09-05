package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;
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
 * Pure unit tests for ContentProductionCompanyController.
 * <p>
 * Notes:
 * - We DO NOT start Spring (no MockMvc, no context).
 * - Security annotations (@PreAuthorize) are NOT evaluated here.
 * - Bean Validation annotations on DTOs are NOT executed in unit tests.
 * - We call controller methods directly and verify interactions with ContentService.
 */
@ExtendWith(MockitoExtension.class)
class ContentProductionCompanyControllerTest {

    @Mock
    private ContentService contentService;

    private ContentProductionCompanyController controller;

    @BeforeEach
    void setUp() {
        controller = new ContentProductionCompanyController(contentService);
    }

    @Test
    void addContent_returnsOkAndBodyFromService() {
        // Arrange
        ProductionCompany principal = new ProductionCompany(); // acts as @AuthenticationPrincipal
        ContentRequestDTO req = mock(ContentRequestDTO.class);
        ContentResponseDTO expected = mock(ContentResponseDTO.class);

        when(contentService.create(principal, req)).thenReturn(expected);

        // Act
        ResponseEntity<ContentResponseDTO> res = controller.addContent(principal, req);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).create(principal, req);
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void updateContent_returnsOkAndBodyFromService() {
        // Arrange
        ProductionCompany principal = new ProductionCompany();
        Long id = 123L;
        ContentRequestDTO req = mock(ContentRequestDTO.class);
        ContentResponseDTO expected = mock(ContentResponseDTO.class);

        when(contentService.update(principal, id, req)).thenReturn(expected);

        // Act
        ResponseEntity<ContentResponseDTO> res = controller.updateContent(principal, id, req);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).update(principal, id, req);
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void getMyContents_returnsOkWithListFromService() {
        // Arrange
        ProductionCompany principal = new ProductionCompany();
        List<ContentResponseDTO> expected = List.of(mock(ContentResponseDTO.class), mock(ContentResponseDTO.class));

        when(contentService.getByProductionCompany(principal)).thenReturn(expected);

        // Act
        ResponseEntity<List<ContentResponseDTO>> res = controller.getMyContents(principal);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).getByProductionCompany(principal);
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void deleteContent_returnsOkWithCustomResponse() {
        // Arrange
        ProductionCompany principal = new ProductionCompany();
        Long id = 99L;
        CustomResponseDTO expected = mock(CustomResponseDTO.class);

        when(contentService.delete(principal, id)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> res = controller.deleteContent(principal, id);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());
        verify(contentService).delete(principal, id);
        verifyNoMoreInteractions(contentService);
    }

    @Test
    void endpoints_returnOkEvenWhenServiceReturnsEmptyLists() {
        // Arrange
        ProductionCompany principal = new ProductionCompany();
        when(contentService.getByProductionCompany(principal)).thenReturn(List.of());

        // Act (call once and reuse the response to avoid double invocations)
        var res = controller.getMyContents(principal);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().isEmpty());
        verify(contentService).getByProductionCompany(principal);
        verifyNoMoreInteractions(contentService);
    }
}