package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserPasswordChangeRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.service.definition.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

/**
 * Pure unit tests for UserController.
 * <p>
 * Characteristics:
 * - No Spring context (no @SpringBootTest, no MockMvc).
 * - UserService is mocked and injected into the controller.
 * - Validation annotations (@Valid, @Validated) are NOT executed in unit tests.
 * - We directly call controller methods and verify service interactions.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /**
     * Simple concrete subclass of UserRequestDTO,
     * used only for testing purposes since UserRequestDTO is abstract.
     */
    static class TestUserRequestDTO extends UserRequestDTO { }

    @Mock
    private UserService<User, TestUserRequestDTO, UserResponseDTO> userService;

    @InjectMocks
    private UserController<User, TestUserRequestDTO, UserResponseDTO> controller;

    @Test
    void getCurrentUser_returnsDto() {
        // Arrange: mock authenticated user and expected DTO
        User principal = mock(User.class);
        UserResponseDTO dto = new UserResponseDTO();
        when(userService.getCurrentUserDto(principal)).thenReturn(dto);

        // Act: call controller method
        ResponseEntity<UserResponseDTO> res = controller.getCurrentUser(principal);

        // Assert: response contains the expected DTO
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
        verify(userService).getCurrentUserDto(principal);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getById_returnsDto() {
        // Arrange
        Long id = 42L;
        UserResponseDTO dto = new UserResponseDTO();
        when(userService.getById(id)).thenReturn(dto);

        // Act
        ResponseEntity<UserResponseDTO> res = controller.getById(id);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
        verify(userService).getById(id);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getByUsernameOrEmail_returnsDto() {
        // Arrange
        String key = "matteo@example.com";
        UserResponseDTO dto = new UserResponseDTO();
        when(userService.getByUsernameOrEmail(key)).thenReturn(dto);

        // Act
        ResponseEntity<UserResponseDTO> res = controller.getByUsernameOrEmail(key);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
        verify(userService).getByUsernameOrEmail(key);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void update_returnsLoginResponse() {
        // Arrange
        User principal = mock(User.class);
        TestUserRequestDTO req = new TestUserRequestDTO();
        UserLoginResponseDTO login = new UserLoginResponseDTO("jwt-access");
        when(userService.update(principal, req)).thenReturn(login);

        // Act
        ResponseEntity<UserLoginResponseDTO> res = controller.update(principal, req);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(login, res.getBody());
        verify(userService).update(principal, req);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changePassword_returnsOk() {
        // Arrange
        User principal = mock(User.class);
        UserPasswordChangeRequestDTO req = new UserPasswordChangeRequestDTO();
        CustomResponseDTO ok = new CustomResponseDTO("Password changed");
        when(userService.changePassword(principal, req)).thenReturn(ok);

        // Act
        ResponseEntity<CustomResponseDTO> res = controller.changePassword(principal, req);

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("Password changed", res.getBody().getMessage());
        verify(userService).changePassword(principal, req);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAll_returnsListFromService() {
        // Arrange
        var dto = new UserResponseDTO();
        when(userService.getAll()).thenReturn(List.of(dto));

        // Act
        var res = controller.getAll();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().size());
        assertSame(dto, res.getBody().getFirst());
        verify(userService).getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAll_returnsEmptyList() {
        // Arrange
        when(userService.getAll()).thenReturn(List.of());

        // Act
        var res = controller.getAll();

        // Assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().isEmpty());
        verify(userService).getAll();
        verifyNoMoreInteractions(userService);
    }
}