package janus.auth.jose.jwk.domain.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class CertificateNotFoundException extends JanusBaseException {

    public CertificateNotFoundException(String kid) {
        super(
                "Certificate not found for kid: " + kid,
                "CERTIFICATE_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
