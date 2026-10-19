package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class CertificateIssuanceException extends JanusBaseException {

    public CertificateIssuanceException(String message) {
        super(message, "CERTIFICATE_ISSUANCE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public CertificateIssuanceException(String message, Throwable cause) {
        super(message, cause, "CERTIFICATE_ISSUANCE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
