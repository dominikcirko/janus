package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidRequestException extends JanusBaseException {
    public InvalidRequestException(String message) {
        super(
                message,
                "INVALID_REQUEST",
                HttpStatus.BAD_REQUEST,
                "Check your request parameters",
                "Refer to API documentation for correct format"
        );
    }
}
