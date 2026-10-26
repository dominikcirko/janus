package janus.auth.oauth2.client.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

/**
 * Carries an RFC registration error code to the registration error response.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
 */
public final class RegisteredClientException extends JanusBaseException {

    public RegisteredClientException(RegisteredClientError error, String message) {
        super(message, error.value(), HttpStatus.BAD_REQUEST);
    }
}
