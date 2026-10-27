package janus.auth.oauth2.authorization.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import janus.auth.oauth2.authorization.domain.exception.AuthorizationException;
import janus.common.exceptionhandler.dto.ApiResponse;
import janus.common.exceptionhandler.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles authorization errors separately because RFC 6749 requires either an error redirect
 * to a validated client URI or a local error when the client or redirect URI cannot be trusted.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "janus.auth.oauth2.authorization")
public class AuthorizationExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(
            AuthorizationException ex,
            HttpServletRequest request
    ) {
        log.warn("Authorization request rejected: {}", ex.getMessage());

        if (ex.getValidatedRedirectUri() == null) {
            return localErrorResponse(ex, request);
        }

        var redirectUri = UriComponentsBuilder
                .fromUri(ex.getValidatedRedirectUri())
                .queryParam("error", ex.getErrorCode())
                .queryParam("error_description", ex.getMessage());

        if (ex.getState() != null) {
            redirectUri.queryParam("state", ex.getState());
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(redirectUri.build().encode().toUri())
                .cacheControl(CacheControl.noStore())
                .build();
    }

    private ResponseEntity<ApiResponse<Void>> localErrorResponse(
            AuthorizationException ex,
            HttpServletRequest request
    ) {
        var errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .statusCode(ex.getHttpStatus().value())
                .build();
        var response = ApiResponse.<Void>error(errorDetails, request.getRequestURI());

        return ResponseEntity
                .status(ex.getHttpStatus())
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
