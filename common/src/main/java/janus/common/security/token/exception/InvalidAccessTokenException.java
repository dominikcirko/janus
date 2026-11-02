package janus.common.security.token.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidAccessTokenException extends JanusBaseException {
    public static final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";

    public InvalidAccessTokenException(String message) {
        super(message, INVALID_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    public InvalidAccessTokenException(String message, Throwable cause) {
        super(message, cause, INVALID_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
    }
}
