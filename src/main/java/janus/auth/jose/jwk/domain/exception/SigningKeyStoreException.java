package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class SigningKeyStoreException extends JanusBaseException {

    public SigningKeyStoreException(String message) {
        super(message, "SIGNING_KEY_STORE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SigningKeyStoreException(String message, Throwable cause) {
        super(message, cause, "SIGNING_KEY_STORE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
