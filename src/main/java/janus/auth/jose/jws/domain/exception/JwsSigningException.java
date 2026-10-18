package janus.auth.jose.jws.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public class JwsSigningException extends JanusBaseException {
    public JwsSigningException(String message) {
        super(
                message,
                "JWS_SIGNING_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Provide valid token issuance values"
        );
    }
}
