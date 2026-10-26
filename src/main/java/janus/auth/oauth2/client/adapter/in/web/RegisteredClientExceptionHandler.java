package janus.auth.oauth2.client.adapter.in.web;

import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientErrorResponse;
import janus.auth.oauth2.client.domain.exception.RegisteredClientError;
import janus.auth.oauth2.client.domain.exception.RegisteredClientException;
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
 * This advice is scoped to {@link RegisteredClientController}, so other endpoints continue
 * to use the standard Janus error response.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = RegisteredClientController.class)
public class RegisteredClientExceptionHandler {

    /**
     * Serializes registration failures using the RFC error response shape.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
     */
    @ExceptionHandler(RegisteredClientException.class)
    public ResponseEntity<RegisteredClientErrorResponse> handleRegisteredClientException(
            RegisteredClientException ex) {
        log.warn("Client registration rejected: {}", ex.getMessage());

        var response = new RegisteredClientErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    /**
     * Maps invalid redirect URIs and other invalid metadata to their RFC error codes.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RegisteredClientErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("Client registration validation failed: {}", ex.getMessage());

        var invalidRedirectUri = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .anyMatch(fieldError -> fieldError.getField().startsWith("redirectUris"));

        if (invalidRedirectUri) {
            return errorResponse(
                    RegisteredClientError.INVALID_REDIRECT_URI,
                    "One or more redirect URIs are invalid"
            );
        }

        return errorResponse(
                RegisteredClientError.INVALID_CLIENT_METADATA,
                "One or more client metadata fields are invalid"
        );
    }

    /**
     * Reports malformed registration metadata as {@code invalid_client_metadata}.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RegisteredClientErrorResponse> handleMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("Client registration request body could not be read: {}", ex.getMessage());

        return errorResponse(
                RegisteredClientError.INVALID_CLIENT_METADATA,
                "Registration request contains malformed JSON or invalid metadata values"
        );
    }

    private ResponseEntity<RegisteredClientErrorResponse> errorResponse(
            RegisteredClientError error, String description) {
        var response = new RegisteredClientErrorResponse(error.value(), description);

        return ResponseEntity
                .badRequest()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
