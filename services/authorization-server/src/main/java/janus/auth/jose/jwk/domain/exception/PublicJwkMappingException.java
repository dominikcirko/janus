package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class PublicJwkMappingException extends JanusBaseException {

    public PublicJwkMappingException(String message) {
        super(message, "PUBLIC_JWK_MAPPING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PublicJwkMappingException(String message, Throwable cause) {
        super(message, cause, "PUBLIC_JWK_MAPPING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
