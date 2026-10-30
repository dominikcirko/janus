package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidEntityReferenceException extends JanusBaseException {

    public InvalidEntityReferenceException(String message) {
        super(
                message,
                "INVALID_ENTITY_REFERENCE",
                HttpStatus.BAD_REQUEST
        );
    }
}
