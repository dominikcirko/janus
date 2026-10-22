package janus.auth.jose.jwk.common;

import janus.auth.jose.jwk.domain.exception.PemEncodingException;
import org.junit.jupiter.api.Test;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PemUtilsTest {

    @Test
    void testWhenEncodingError_thenWrapException() throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        var cause = new CertificateEncodingException("Encoding failed");
        when(certificate.getEncoded()).thenThrow(cause);
        var expectedMessage = "Could not encode certificate as PEM";

        var exception = assertThrows(
                PemEncodingException.class,
                () -> PemUtils.encodeCertificate(certificate)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
