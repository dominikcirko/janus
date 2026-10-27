package janus.auth.oauth2.authorization.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Error codes returned by the OAuth authorization endpoint.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
 */
public enum AuthorizationError {
    INVALID_REQUEST("invalid_request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CLIENT("unauthorized_client", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("access_denied", HttpStatus.FORBIDDEN),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type", HttpStatus.BAD_REQUEST),
    INVALID_SCOPE("invalid_scope", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("server_error", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private final String value;
    private final HttpStatus httpStatus;

    AuthorizationError(String value, HttpStatus httpStatus) {
        this.value = value;
        this.httpStatus = httpStatus;
    }

    public String value() {
        return value;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
