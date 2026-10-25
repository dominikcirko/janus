package janus.auth.oauth2.client.application.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class ClientRegistrationException extends JanusBaseException {

    public ClientRegistrationException(ClientRegistrationError error, String message) {
        super(message, error.value(), HttpStatus.BAD_REQUEST);
    }
}
