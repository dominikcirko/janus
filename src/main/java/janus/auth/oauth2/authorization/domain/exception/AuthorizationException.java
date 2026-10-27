package janus.auth.oauth2.authorization.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import lombok.Getter;

import java.net.URI;
import java.util.Objects;

/**
 * Carries an OAuth authorization error and, only after validation, its redirect information.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
 */
@Getter
public final class AuthorizationException extends JanusBaseException {

    private final URI validatedRedirectUri;
    private final String state;

    public AuthorizationException(AuthorizationError error, String message) {
        super(message, error.value(), error.httpStatus());
        this.validatedRedirectUri = null;
        this.state = null;
    }

    /**
     * Creates an error that may be redirected only because {@code validatedRedirectUri} has
     * already been matched against the registered client's redirect URIs.
     */
    public AuthorizationException(
            AuthorizationError error,
            String message,
            URI validatedRedirectUri,
            String state
    ) {
        super(message, error.value(), error.httpStatus());
        this.validatedRedirectUri = Objects.requireNonNull(
                validatedRedirectUri,
                "Validated redirect URI must not be null"
        );
        this.state = state;
    }
}
