package janus.auth.jose.jwt.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class InvalidTokenContextException extends JanusBaseException {
    public InvalidTokenContextException(String message) {
        super(
                message,
                "INVALID_TOKEN_CONTEXT",
                HttpStatus.BAD_REQUEST,
                "Provide valid token issuance values"
        );
    }
}
