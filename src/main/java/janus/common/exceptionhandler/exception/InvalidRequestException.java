package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusException;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends JanusException {
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
