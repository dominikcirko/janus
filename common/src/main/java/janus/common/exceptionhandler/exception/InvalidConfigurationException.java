package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidConfigurationException extends JanusBaseException {

    public InvalidConfigurationException(String message) {
        super(
                message,
                "INVALID_CONFIGURATION",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
