package click.reelscout.backend.exception;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.exception.custom.*;
import click.reelscout.backend.model.jpa.User;
import io.awspring.cloud.s3.S3Exception;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for GlobalExceptionHandler.
 * <p>
 * Characteristics:
 * - No Spring context or MockMvc is started.
 * - Each handler method is invoked directly.
 * - Assertions verify HTTP status and CustomResponseDTO body (null-safety included).
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Unit-under-test: we instantiate the advice directly (no Spring involved).
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("DataValidationException -> 400 Bad Request")
    void handleDataValidationExceptionReturnsBadRequest() {
        // Arrange
        DataValidationException ex = new DataValidationException("Invalid data");

        // Act
        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleDataValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid data", response.getBody().getMessage());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException -> 400 with concatenated messages")
    void handleMethodArgumentNotValidExceptionReturnsBadRequest() {
        // Arrange: simulate two field errors
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("obj", "field1", "msg1");
        FieldError error2 = new FieldError("obj", "field2", "msg2");
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        // Act
        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        String message = response.getBody().getMessage();
        // Current implementation may include a trailing ", " — assert presence of both messages.
        assertTrue(message.contains("msg1"));
        assertTrue(message.contains("msg2"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException with zero errors -> 400 and empty message")
    void handleMethodArgumentNotValidException_noErrors_returnsEmptyMessage() {
        // Arrange: no field errors present
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("", response.getBody().getMessage());
    }

    @Test
    @DisplayName("EntityCreateException -> 400 Bad Request")
    void handleEntityCreateExceptionReturnsBadRequest() {
        EntityCreateException ex = new EntityCreateException("Create error");

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleEntityCreateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Create error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("EntityUpdateException -> 400 Bad Request")
    void handleEntityUpdateExceptionReturnsBadRequest() {
        EntityUpdateException ex = new EntityUpdateException("Update error");

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleEntityUpdateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Update error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("EntityDeleteException -> 400 Bad Request")
    void handleEntityDeleteExceptionReturnsBadRequest() {
        EntityDeleteException ex = new EntityDeleteException("Delete error");

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleEntityDeleteException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Delete error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("EntityNotFoundException -> 404 Not Found (User)")
    void handleEntityNotFoundExceptionReturnsNotFound() {
        // Arrange: use a real domain class (User) for message composition
        EntityNotFoundException ex = new EntityNotFoundException(User.class);

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("InvalidCredentialsException -> 401 Unauthorized")
    void handleInvalidCredentialsExceptionReturnsUnauthorized() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleInvalidCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials provided", response.getBody().getMessage());
    }

    @Test
    @DisplayName("S3Exception -> 500 Internal Server Error")
    void handleS3ExceptionReturnsInternalServerError() {
        S3Exception ex = new S3Exception("S3 failure", null);

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleS3Exception(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("S3 error: S3 failure", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ExpiredJwtException -> 401 Unauthorized")
    void handleExpiredJwtExceptionReturnsUnauthorized() {
        // Arrange: we can mock the exception because we only care about mapping
        ExpiredJwtException ex = mock(ExpiredJwtException.class);

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleExpiredJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access token has expired", response.getBody().getMessage());
    }

    @Test
    @DisplayName("MalformedJwtException / SignatureException -> 401 Unauthorized")
    void handleInvalidJwtExceptionsReturnUnauthorized() {
        // Arrange
        MalformedJwtException malformedEx = mock(MalformedJwtException.class);
        SignatureException signatureEx = mock(SignatureException.class);

        // Act
        ResponseEntity<CustomResponseDTO> res1 = exceptionHandler.handleInvalidJwtException(malformedEx);
        ResponseEntity<CustomResponseDTO> res2 = exceptionHandler.handleInvalidJwtException(signatureEx);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, res1.getStatusCode());
        assertNotNull(res1.getBody());
        assertEquals("Invalid access token", res1.getBody().getMessage());

        assertEquals(HttpStatus.UNAUTHORIZED, res2.getStatusCode());
        assertNotNull(res2.getBody());
        assertEquals("Invalid access token", res2.getBody().getMessage());
    }

    @Test
    @DisplayName("Generic JwtException -> 401 Unauthorized")
    void handleJwtExceptionReturnsUnauthorized() {
        JwtException ex = new JwtException("jwt failed");

        ResponseEntity<CustomResponseDTO> response = exceptionHandler.handleJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Authentication failed", response.getBody().getMessage());
    }
}