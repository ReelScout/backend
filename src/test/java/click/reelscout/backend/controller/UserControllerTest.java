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
 * Unit tests for {@link UserController}.
 * Uses Mockito to mock dependencies and verify interactions.
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

    /**
     * Test for getCurrentUser method.
     * Verifies that the controller calls the service and returns the expected DTO.
     */
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

    /**
     * Test for getById method.
     * Verifies that the controller calls the service with the correct ID and returns the expected DTO.
     */
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

    /**
     * Test for getByUsernameOrEmail method.
     * Verifies that the controller calls the service with the correct key and returns the expected DTO.
     */
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

    /**
     * Test for update method.
     * Verifies that the controller calls the service with the authenticated user and request DTO,
     * and returns the expected login response DTO.
     */
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

    /**
     * Test for changePassword method.
     * Verifies that the controller calls the service with the authenticated user and password change request,
     * and returns a confirmation message.
     */
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

    /**
     * Test for getAll method.
     * Verifies that the controller calls the service and returns the list of user DTOs.
     */
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

    /**
     * Test for getAll method when service returns an empty list.
     * Verifies that the controller correctly handles and returns an empty list.
     */
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