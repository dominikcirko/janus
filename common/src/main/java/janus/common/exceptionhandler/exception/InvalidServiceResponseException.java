package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidServiceResponseException extends JanusBaseException {

    public InvalidServiceResponseException(String message) {
        super(message, "INVALID_SERVICE_RESPONSE", HttpStatus.BAD_GATEWAY);
    }
}
