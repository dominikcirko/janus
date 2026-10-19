package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class SigningKeyGenerationException extends JanusBaseException {

    public SigningKeyGenerationException(String message) {
        super(message, "SIGNING_KEY_GENERATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SigningKeyGenerationException(String message, Throwable cause) {
        super(message, cause, "SIGNING_KEY_GENERATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
