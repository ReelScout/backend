package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.PromotionDecisionRequestDTO;
import click.reelscout.backend.dto.request.PromotionRequestCreateDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.PromotionRequestResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PromotionController.
 *
 * These are pure unit tests: we instantiate the controller with a mocked PromotionService
 * and call its methods directly. This avoids dealing with Spring MVC plumbing and security.
 */
@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

    @Mock
    private PromotionService promotionService;

    private PromotionController controller;

    @BeforeEach
    void setUp() {
        controller = new PromotionController(promotionService);
    }

    @Test
    void requestVerified_returnsOk_andDelegatesToService() {
        // Arrange
        Member requester = mock(Member.class);
        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.requestVerifiedPromotion(requester, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.requestVerified(requester, dto);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).requestVerifiedPromotion(requester, dto);
    }

    @Test
    void listPendingVerified_returnsOk_withListFromService() {
        // Arrange
        List<PromotionRequestResponseDTO> expected = List.of(mock(PromotionRequestResponseDTO.class));
        when(promotionService.listPendingVerifiedRequests()).thenReturn(expected);

        // Act
        ResponseEntity<List<PromotionRequestResponseDTO>> response = controller.listPendingVerified();

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).listPendingVerifiedRequests();
    }

    @Test
    void approveVerified_returnsOk_andDelegatesToService() {
        // Arrange
        User moderator = mock(User.class);
        long id = 10L;
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.approveVerifiedPromotion(moderator, id)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.approveVerified(moderator, id);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).approveVerifiedPromotion(moderator, id);
    }

    @Test
    void rejectVerified_returnsOk_andDelegatesToService() {
        // Arrange
        User moderator = mock(User.class);
        long id = 11L;
        PromotionDecisionRequestDTO dto = new PromotionDecisionRequestDTO();
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.rejectVerifiedPromotion(moderator, id, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.rejectVerified(moderator, id, dto);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).rejectVerifiedPromotion(moderator, id, dto);
    }

    @Test
    void requestModerator_returnsOk_andDelegatesToService() {
        // Arrange
        Member requester = mock(Member.class);
        PromotionRequestCreateDTO dto = new PromotionRequestCreateDTO();
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.requestModeratorPromotion(requester, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.requestModerator(requester, dto);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).requestModeratorPromotion(requester, dto);
    }

    @Test
    void listPendingModerator_returnsOk_withListFromService() {
        // Arrange
        List<PromotionRequestResponseDTO> expected = List.of(mock(PromotionRequestResponseDTO.class));
        when(promotionService.listPendingModeratorRequests()).thenReturn(expected);

        // Act
        ResponseEntity<List<PromotionRequestResponseDTO>> response = controller.listPendingModerator();

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).listPendingModeratorRequests();
    }

    @Test
    void approveModerator_returnsOk_andDelegatesToService() {
        // Arrange
        User admin = mock(User.class);
        long id = 20L;
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.approveModeratorPromotion(admin, id)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.approveModerator(admin, id);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).approveModeratorPromotion(admin, id);
    }

    @Test
    void rejectModerator_returnsOk_andDelegatesToService() {
        // Arrange
        User admin = mock(User.class);
        long id = 21L;
        PromotionDecisionRequestDTO dto = new PromotionDecisionRequestDTO();
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.rejectModeratorPromotion(admin, id, dto)).thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.rejectModerator(admin, id, dto);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).rejectModeratorPromotion(admin, id, dto);
    }

    @Test
    void myRequests_returnsOk_withListFromService() {
        // Arrange
        Member requester = mock(Member.class);
        List<PromotionRequestResponseDTO> expected = List.of(mock(PromotionRequestResponseDTO.class));
        when(promotionService.myRequests(requester)).thenReturn(expected);

        // Act
        ResponseEntity<List<PromotionRequestResponseDTO>> response = controller.myRequests(requester);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).myRequests(requester);
    }

    @Test
    void requestVerified_allowsNullBody_andDelegatesToService() {
        // Arrange
        Member requester = mock(Member.class);
        CustomResponseDTO expected = mock(CustomResponseDTO.class);
        when(promotionService.requestVerifiedPromotion(eq(requester), ArgumentMatchers.isNull()))
                .thenReturn(expected);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.requestVerified(requester, null);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(promotionService).requestVerifiedPromotion(eq(requester), ArgumentMatchers.isNull());
    }
}