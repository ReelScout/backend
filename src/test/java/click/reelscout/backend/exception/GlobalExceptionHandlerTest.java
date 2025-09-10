package click.reelscout.backend.exception;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.exception.custom.*;
import io.awspring.cloud.s3.S3Exception;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for GlobalExceptionHandler.
 * We verify the returned HTTP status codes and CustomResponseDTO messages for each handler.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // --------------------- 400 Bad Request custom exceptions ---------------------

    @Test
    void handleDataValidationException_shouldReturn400_withMessage() {
        DataValidationException ex = mock(DataValidationException.class);
        when(ex.getMessage()).thenReturn("Invalid data");
        ResponseEntity<CustomResponseDTO> resp = handler.handleDataValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Invalid data", resp.getBody().getMessage());
    }

    @Test
    void handleEntityCreateException_shouldReturn400_withMessage() {
        EntityCreateException ex = mock(EntityCreateException.class);
        when(ex.getMessage()).thenReturn("Create failed");
        ResponseEntity<CustomResponseDTO> resp = handler.handleEntityCreateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Create failed", resp.getBody().getMessage());
    }

    @Test
    void handleEntityUpdateException_shouldReturn400_withMessage() {
        EntityUpdateException ex = mock(EntityUpdateException.class);
        when(ex.getMessage()).thenReturn("Update failed");
        ResponseEntity<CustomResponseDTO> resp = handler.handleEntityUpdateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Update failed", resp.getBody().getMessage());
    }

    @Test
    void handleEntityDeleteException_shouldReturn400_withMessage() {
        EntityDeleteException ex = mock(EntityDeleteException.class);
        when(ex.getMessage()).thenReturn("Delete failed");
        ResponseEntity<CustomResponseDTO> resp = handler.handleEntityDeleteException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Delete failed", resp.getBody().getMessage());
    }

    // --------------------- 404 Not Found ---------------------

    @Test
    void handleNotFoundException_shouldReturn404_withMessage() {
        EntityNotFoundException ex = mock(EntityNotFoundException.class);
        when(ex.getMessage()).thenReturn("Not found");
        ResponseEntity<CustomResponseDTO> resp = handler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Not found", resp.getBody().getMessage());
    }

    // --------------------- 401 Unauthorized (credentials/JWT) ---------------------

    @Test
    void handleInvalidCredentialsException_shouldReturn401_withMessage() {
        InvalidCredentialsException ex = mock(InvalidCredentialsException.class);
        when(ex.getMessage()).thenReturn("Bad credentials");
        ResponseEntity<CustomResponseDTO> resp = handler.handleInvalidCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Bad credentials", resp.getBody().getMessage());
    }

    @Test
    void handleExpiredJwtException_shouldReturn401_withFixedMessage() {
        // ExpiredJwtException has complex constructors; use a mock
        ExpiredJwtException ex = mock(ExpiredJwtException.class);
        ResponseEntity<CustomResponseDTO> resp = handler.handleExpiredJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Access token has expired", resp.getBody().getMessage(),
                "Handler should return a fixed, user-friendly message");
    }

    @Test
    void handleInvalidJwtException_shouldReturn401_withGenericInvalidTokenMessage() {
        // The handler is mapped to MalformedJwtException and SignatureException, but takes JwtException
        JwtException ex = mock(MalformedJwtException.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        ResponseEntity<CustomResponseDTO> resp = handler.handleInvalidJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Invalid access token", resp.getBody().getMessage());
    }

    @Test
    void handleJwtException_shouldReturn401_withGenericAuthFailedMessage() {
        JwtException ex = mock(JwtException.class);
        ResponseEntity<CustomResponseDTO> resp = handler.handleJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Authentication failed", resp.getBody().getMessage());
    }

    // --------------------- 500 Internal Server Error ---------------------

    @Test
    void handleS3Exception_shouldReturn500_withPrefixedMessage() {
        S3Exception ex = mock(S3Exception.class);
        when(ex.getMessage()).thenReturn("bucket unreachable");
        ResponseEntity<CustomResponseDTO> resp = handler.handleS3Exception(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("S3 error: bucket unreachable", resp.getBody().getMessage());
    }

    @Test
    void handleSearchException_shouldReturn500_withMessage() {
        SearchException ex = mock(SearchException.class);
        when(ex.getMessage()).thenReturn("Index failure");
        ResponseEntity<CustomResponseDTO> resp = handler.handleSearchException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Index failure", resp.getBody().getMessage());
    }

    // --------------------- 400 Bad Request (validation errors aggregation) ---------------------

    @Test
    void handleMethodArgumentNotValidException_shouldAggregateMessages_andReturn400(){
        // Create a real BindingResult with two field errors
        Object target = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field1", "first error"));
        bindingResult.addError(new FieldError("target", "field2", "second error"));

        // Build the exception using a mocked MethodParameter
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<CustomResponseDTO> resp = handler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
        // The handler joins messages as "<msg>, " and leaves a trailing comma+space
        assertEquals("first error, second error, ", resp.getBody().getMessage());
    }
}