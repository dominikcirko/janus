package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class JwkEncodingException extends JanusBaseException {

    public JwkEncodingException(String message) {
        super(message, "JWK_ENCODING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public JwkEncodingException(String message, Throwable cause) {
        super(message, cause, "JWK_ENCODING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
