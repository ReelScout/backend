package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.UserLoginRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.UserLoginResponseDTO;
import click.reelscout.backend.service.definition.AuthService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for AuthController:
 * - No Spring context or MockMvc.
 * - Controller is instantiated directly with a mocked AuthService.
 * - Validation annotations (@Valid/@Validated) are not triggered here.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService<UserRequestDTO> authService;

    private AuthController<UserRequestDTO> controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController<>(authService);
    }

    @Test
    void login_validRequest_returnsOkAndBodyFromService() {
        // Arrange
        UserLoginRequestDTO req = new UserLoginRequestDTO();
        req.setUsername("user@mail.com");
        req.setPassword("pwd123");

        UserLoginResponseDTO expected = new UserLoginResponseDTO("jwt-token-123");
        when(authService.login(req)).thenReturn(expected);

        // Act
        ResponseEntity<UserLoginResponseDTO> response = controller.login(req);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(authService).login(req);
        verifyNoMoreInteractions(authService);
    }

    @Test
    void register_validRequest_returnsOkAndBodyFromService() {
        // Arrange: anonymous subclass of UserRequestDTO with getters overridden
        UserRequestDTO registerReq = new UserRequestDTO() {
            @Override
            public String getUsername() { return "matteo"; }

            @Override
            public String getEmail() { return "matteo@mail.com"; }

            @Override
            public String getPassword() { return "pwd123"; }
        };

        UserLoginResponseDTO expected = new UserLoginResponseDTO("jwt-token-xyz");
        when(authService.register(registerReq)).thenReturn(expected);

        // Act
        ResponseEntity<UserLoginResponseDTO> response = controller.register(registerReq);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(authService).register(registerReq);
        verifyNoMoreInteractions(authService);
    }

    @Test
    void register_serviceThrowsConstraintViolation_controllerPropagates() {
        // Arrange: simulate a validation failure thrown by the service
        UserRequestDTO badReq = new UserRequestDTO() {};
        badReq.setUsername("");
        badReq.setEmail("");
        badReq.setPassword("");

        var emptyViolations = Collections.<ConstraintViolation<UserRequestDTO>>emptySet();
        when(authService.register(any(UserRequestDTO.class)))
                .thenThrow(new ConstraintViolationException("validation failed", emptyViolations));

        // Act & Assert: controller should propagate the exception
        assertThrows(ConstraintViolationException.class, () -> controller.register(badReq));
        verify(authService).register(any(UserRequestDTO.class));
        verifyNoMoreInteractions(authService);
    }
}