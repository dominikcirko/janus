package janus.auth.oauth2.client.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class RegisteredClientException extends JanusBaseException {

    public RegisteredClientException(RegisteredClientError error, String message) {
        super(message, error.value(), HttpStatus.BAD_REQUEST);
    }
}
