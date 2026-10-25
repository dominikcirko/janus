package janus.auth.oauth2.client.adapter.in.web;

import janus.auth.oauth2.client.application.dto.ClientRegistrationErrorResponse;
import janus.auth.oauth2.client.application.exception.ClientRegistrationError;
import janus.auth.oauth2.client.application.exception.ClientRegistrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles client-registration errors separately from the global exception handler because
 * RFC 7591 defines its own {@code error} and {@code error_description} response format.
 * This advice is scoped to {@link ClientRegistrationController}, so other endpoints continue
 * to use the standard Janus error response.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ClientRegistrationController.class)
public class ClientRegistrationExceptionHandler {

    @ExceptionHandler(ClientRegistrationException.class)
    public ResponseEntity<ClientRegistrationErrorResponse> handleClientRegistrationException(
            ClientRegistrationException ex) {
        log.warn("Client registration rejected: {}", ex.getMessage());

        var response = new ClientRegistrationErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ClientRegistrationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("Client registration validation failed: {}", ex.getMessage());

        var error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .anyMatch(fieldError -> fieldError.getField().startsWith("redirectUris"))
                ? ClientRegistrationError.INVALID_REDIRECT_URI
                : ClientRegistrationError.INVALID_CLIENT_METADATA;

        var validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        var description = validationErrors.isEmpty()
                ? "Invalid client metadata"
                : String.join("; ", validationErrors);

        return errorResponse(error, description);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ClientRegistrationErrorResponse> handleMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("Client registration request body could not be read: {}", ex.getMessage());

        return errorResponse(
                ClientRegistrationError.INVALID_CLIENT_METADATA,
                "Registration request contains malformed JSON or invalid metadata values"
        );
    }

    private ResponseEntity<ClientRegistrationErrorResponse> errorResponse(
            ClientRegistrationError error, String description) {
        var response = new ClientRegistrationErrorResponse(error.value(), description);

        return ResponseEntity
                .badRequest()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
