package janus.auth.jose.jwk.common;

import janus.auth.jose.jwk.domain.exception.JwkEncodingException;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwkUtilsTest {

    @Test
    void testWhenEncodingError_thenWrapException() throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        var cause = new CertificateEncodingException("Encoding failed");
        when(certificate.getEncoded()).thenThrow(cause);
        var expectedMessage = "Could not encode certificate for JWK x5c";

        var exception = assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.encodeCertificate(certificate)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testWhenDigestAlgorithmDoesNotExist_thenWrapException()
            throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        when(certificate.getEncoded()).thenReturn(new byte[]{1, 2, 3});
        var expectedMessage = "Could not calculate certificate thumbprint";

        var exception = assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.computeX5t(certificate, "NOT-A-REAL-DIGEST-ALGORITHM")
        );
        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
    }
}
