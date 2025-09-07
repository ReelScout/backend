package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.WatchlistService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for WatchlistController.
 * These tests focus on verifying:
 *  - correct delegation to WatchlistService,
 *  - correct wrapping of service results in ResponseEntity.ok(...),
 *  - correct propagation of exceptions.
 * No Spring context, no MockMvc: just a plain Mockito-based unit test.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistControllerTest {

    @Mock
    private WatchlistService watchlistService;

    @InjectMocks
    private WatchlistController controller;

    // --- Helpers -----------------------------------------------------------------

    /**
     * Creates a lightweight Member instance.
     * Keep it minimal as controller forwards it to the service as-is.
     */
    private Member sampleMember() {
        return mock(Member.class);
    }

    // --- Tests -------------------------------------------------------------------

    @Nested
    class AddWatchlist {

        @Test
        void shouldDelegateToServiceAndReturnOkResponse() {
            // Arrange
            Member member = sampleMember();
            WatchlistRequestDTO request = mock(WatchlistRequestDTO.class);
            WatchlistResponseDTO expected = mock(WatchlistResponseDTO.class);

            when(watchlistService.create(member, request)).thenReturn(expected);

            // Act
            ResponseEntity<WatchlistResponseDTO> response = controller.addWatchlist(member, request);

            // Assert
            // Assert that the controller returns the same object the service returned
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);

            // Verify correct service interaction
            verify(watchlistService).create(member, request);
            verifyNoMoreInteractions(watchlistService);
        }

        @Test
        void shouldPropagateServiceException() {
            // Arrange
            Member member = sampleMember();
            WatchlistRequestDTO request = mock(WatchlistRequestDTO.class);

            when(watchlistService.create(member, request))
                    .thenThrow(new IllegalArgumentException("invalid data"));

            // Act & Assert
            // Controller should not swallow exceptions raised by the service
            assertThatThrownBy(() -> controller.addWatchlist(member, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("invalid data");
        }
    }

    @Nested
    class UpdateWatchlist {

        @Test
        void shouldDelegateUpdateAndReturnOkResponse() {
            // Arrange
            Member member = sampleMember();
            Long id = 42L;
            WatchlistRequestDTO request = mock(WatchlistRequestDTO.class);
            WatchlistResponseDTO expected = mock(WatchlistResponseDTO.class);

            when(watchlistService.update(member, id, request)).thenReturn(expected);

            // Act
            ResponseEntity<WatchlistResponseDTO> response =
                    controller.updateWatchlist(member, id, request);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).update(member, id, request);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class DeleteWatchlist {

        @Test
        void shouldDelegateDeleteAndReturnOkResponse() {
            // Arrange
            Member member = sampleMember();
            Long id = 99L;
            CustomResponseDTO expected = mock(CustomResponseDTO.class);

            when(watchlistService.delete(member, id)).thenReturn(expected);

            // Act
            ResponseEntity<CustomResponseDTO> response = controller.deleteWatchlist(member, id);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).delete(member, id);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class GetMyWatchlists {

        @Test
        void shouldReturnAllWatchlistsForMember() {
            // Arrange
            Member member = sampleMember();
            List<WatchlistResponseDTO> expected = List.of(
                    mock(WatchlistResponseDTO.class),
                    mock(WatchlistResponseDTO.class)
            );

            when(watchlistService.getAllByMember(member)).thenReturn(expected);

            // Act
            ResponseEntity<List<WatchlistResponseDTO>> response = controller.getWatchlists(member);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).getAllByMember(member);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class AddContentToWatchlist {

        @Test
        void shouldAddContentAndReturnUpdatedWatchlist() {
            // Arrange
            Member member = sampleMember();
            Long watchlistId = 7L;
            Long contentId = 13L;
            WatchlistResponseDTO expected = mock(WatchlistResponseDTO.class);

            when(watchlistService.addContentToWatchlist(member, watchlistId, contentId)).thenReturn(expected);

            // Act
            ResponseEntity<WatchlistResponseDTO> response =
                    controller.addContentToWatchlist(member, watchlistId, contentId);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).addContentToWatchlist(member, watchlistId, contentId);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class RemoveContentFromWatchlist {

        @Test
        void shouldRemoveContentAndReturnUpdatedWatchlist() {
            // Arrange
            Member member = sampleMember();
            Long watchlistId = 7L;
            Long contentId = 13L;
            WatchlistResponseDTO expected = mock(WatchlistResponseDTO.class);

            when(watchlistService.removeContentFromWatchlist(member, watchlistId, contentId)).thenReturn(expected);

            // Act
            ResponseEntity<WatchlistResponseDTO> response =
                    controller.removeContentFromWatchlist(member, watchlistId, contentId);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).removeContentFromWatchlist(member, watchlistId, contentId);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class GetWatchlistById {

        @Test
        void shouldReturnWatchlistById() {
            // Arrange
            Member member = sampleMember();
            Long id = 5L;
            WatchlistResponseDTO expected = mock(WatchlistResponseDTO.class);

            when(watchlistService.getById(member, id)).thenReturn(expected);

            // Act
            ResponseEntity<WatchlistResponseDTO> response = controller.getWatchlistById(member, id);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).getById(member, id);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class GetWatchlistsByContent {

        @Test
        void shouldReturnWatchlistsThatContainContent() {
            // Arrange
            Member member = sampleMember();
            Long contentId = 123L;
            List<WatchlistResponseDTO> expected = List.of(mock(WatchlistResponseDTO.class));

            when(watchlistService.getAllByMemberAndContent(member, contentId)).thenReturn(expected);

            // Act
            ResponseEntity<List<WatchlistResponseDTO>> response =
                    controller.getWatchlistsByContent(member, contentId);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).getAllByMemberAndContent(member, contentId);
            verifyNoMoreInteractions(watchlistService);
        }
    }

    @Nested
    class GetPublicWatchlistsByMember {

        @Test
        void shouldReturnPublicWatchlistsForGivenMemberId() {
            // Arrange
            Long memberId = 321L;
            List<WatchlistResponseDTO> expected = List.of(
                    mock(WatchlistResponseDTO.class),
                    mock(WatchlistResponseDTO.class)
            );

            when(watchlistService.getAllPublicByMember(memberId)).thenReturn(expected);

            // Act
            ResponseEntity<List<WatchlistResponseDTO>> response =
                    controller.getPublicWatchlistsByMember(memberId);

            // Assert
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isSameAs(expected);
            verify(watchlistService).getAllPublicByMember(memberId);
            verifyNoMoreInteractions(watchlistService);
        }
    }
}