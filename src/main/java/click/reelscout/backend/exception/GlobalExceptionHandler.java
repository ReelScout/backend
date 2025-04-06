package click.reelscout.backend.exception;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.exception.custom.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles {@link DataValidationException} thrown when data validation fails.
     * <p>
     * This method intercepts {@code DataValidationException} and returns an HTTP 400 (Bad Request)
     * response. The response body contains a {@link CustomResponseDTO} initialized with the exception's message.
     * </p>
     *
     * @param e the {@code DataValidationException} that triggered this handler
     * @return a {@link ResponseEntity} containing a {@link CustomResponseDTO} with the error message and a 400 status
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<CustomResponseDTO> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body(new CustomResponseDTO(e.getMessage()));
    }

    /**
     * Handles {@link MethodArgumentNotValidException} exceptions that occur when method arguments fail validation.
     * <p>
     * This method extracts the default error messages from the field errors contained in the exception's binding result,
     * concatenates them into a single comma-separated string, and returns a {@link ResponseEntity} with an HTTP 400
     * (Bad Request) status code. The response body is a {@link CustomResponseDTO} containing the concatenated validation messages.
     * </p>
     *
     * @param e the {@link MethodArgumentNotValidException} containing the validation errors
     * @return a {@link ResponseEntity} with a 400 status code and a {@link CustomResponseDTO} with the error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String validationMessages = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .reduce("", (acc, message) -> acc + message + ", ");
        return ResponseEntity.badRequest().body(new CustomResponseDTO(validationMessages));
    }

    /**
     * Handles {@link EntityCreateException} exceptions that occur when entity creation fails.
     * <p>
     * This method returns an HTTP 400 (Bad Request) response with a {@link CustomResponseDTO} containing the exception's message.
     * </p>
     *
     * @param e the {@link EntityCreateException} that triggered this handler
     * @return a {@link ResponseEntity} with a 400 status code and a {@link CustomResponseDTO} with the error message
     */
    @ExceptionHandler(EntityCreateException.class)
    public ResponseEntity<CustomResponseDTO> handleEntityCreateException(EntityCreateException e) {
        return ResponseEntity.badRequest().body(new CustomResponseDTO(e.getMessage()));
    }

    /**
     * Handles {@link EntityUpdateException} exceptions that occur when entity modification fails.
     * <p>
     * This method returns an HTTP 400 (Bad Request) response with a {@link CustomResponseDTO} containing the exception's message.
     * </p>
     *
     * @param e the {@link EntityUpdateException} that triggered this handler
     * @return a {@link ResponseEntity} with a 400 status code and a {@link CustomResponseDTO} with the error message
     */
    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<CustomResponseDTO> handleEntityUpdateException(EntityUpdateException e) {
        return ResponseEntity.badRequest().body(new CustomResponseDTO(e.getMessage()));
    }

    /**
     * Handles {@link EntityDeleteException} exceptions that occur when entity deletion fails.
     * <p>
     * This method returns an HTTP 400 (Bad Request) response with a {@link CustomResponseDTO} containing the exception's message.
     * </p>
     *
     * @param e the {@link EntityDeleteException} that triggered this handler
     * @return a {@link ResponseEntity} with a 400 status code and a {@link CustomResponseDTO} with the error message
     */
    @ExceptionHandler(EntityDeleteException.class)
    public ResponseEntity<CustomResponseDTO> handleEntityDeleteException(EntityDeleteException e) {
        return ResponseEntity.badRequest().body(new CustomResponseDTO(e.getMessage()));
    }

    /**
     * Handles {@link EntityNotFoundException} exceptions that occur when an entity is not found.
     * <p>
     * This method returns an HTTP 404 (Not Found) response with a {@link CustomResponseDTO} containing the exception's message.
     * </p>
     *
     * @param e the {@link EntityNotFoundException} that triggered this handler
     * @return a {@link ResponseEntity} with a 404 status code and a {@link CustomResponseDTO} with the error message
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomResponseDTO> handleNotFoundException(EntityNotFoundException e) {
        CustomResponseDTO response = new CustomResponseDTO(e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
