package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class PemEncodingException extends JanusBaseException {

    public PemEncodingException(String message) {
        super(message, "PEM_ENCODING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PemEncodingException(String message, Throwable cause) {
        super(message, cause, "PEM_ENCODING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
