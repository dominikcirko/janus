package janus.auth.jose.jwk.common;

import janus.auth.jose.jwk.domain.exception.JwkEncodingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwkUtilsTest {


    @ParameterizedTest
    @CsvSource({
            //left  = input integer
            //right = expected unpadded Base64URL output
            "0, AA",
            "128, gA",
            "255, _w",
            "65537, AQAB"
    })
    void testWhenValidNonNegativeInteger_thenReturnEncoded(String value, String expected) {
        var result = JwkUtils.encodeBase64UrlUInt(new BigInteger(value));
        assertEquals(expected, result);
    }

    @Test
    void testWhenNegativeValue_thenThrow(){
        assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.encodeBase64UrlUInt(BigInteger.valueOf(-1L))
        );
    }

    @Test
    void testWhenCertificateEncodingValid_thenReturnEncoded() throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        // x5c requires regular Base64 with padding.
        // FB FF becomes +/8= in Base64 and -_8 in unpadded Base64URL,
        // so this input exposes accidental use of the wrong encoder.
        when(certificate.getEncoded()).thenReturn(new byte[]{(byte) 0xFB, (byte) 0xFF});
        var expected = "+/8=";

        var result = JwkUtils.encodeCertificate(certificate);

        assertEquals(expected, result);
    }

    @Test
    void testWhenCertificateEncodingError_thenWrapException() throws CertificateEncodingException {
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


    @ParameterizedTest
    @CsvSource({
            //left  = digest algorithm
            //right = expected unpadded Base64URL certificate thumbprint
            "SHA-1, qZk-NkcGgWq6PiVxeFDCbJzQ2J0",
            "SHA-256, ungWv48Bz-pBQUDeXa4iI7ADYaOWF3qctBD_YfIAFa0"
    })
    void testWhenDigestAlgorithmExists_thenReturnCertificateThumbprint(
            String digestAlgorithm,
            String expected
    ) throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        when(certificate.getEncoded())
                .thenReturn("abc".getBytes(StandardCharsets.US_ASCII));

        var result = JwkUtils.computeX5t(certificate, digestAlgorithm);

        assertEquals(expected, result);
    }

    @Test
    void testWhenDigestAlgorithmNotExists_thenWrapException()
            throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        when(certificate.getEncoded()).thenReturn(new byte[]{1, 2, 3});
        var expectedMessage = "Could not calculate certificate thumbprint using NOT-A-REAL-DIGEST-ALGORITHM";

        var exception = assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.computeX5t(certificate, "NOT-A-REAL-DIGEST-ALGORITHM")
        );
        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
    }

    @Test
    void testWhenCertificateEncodingErrorDuringThumbprint_thenWrapException()
            throws CertificateEncodingException {
        var certificate = mock(X509Certificate.class);
        var cause = new CertificateEncodingException("Encoding failed");
        when(certificate.getEncoded()).thenThrow(cause);
        var digestAlgorithm = "SHA-256";
        var expectedMessage = "Could not calculate certificate thumbprint using " + digestAlgorithm;

        var exception = assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.computeX5t(certificate, digestAlgorithm)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @ParameterizedTest
    @CsvSource({
            //left  = EC field size in bits
            //right = expected coordinate length in bytes
            "256, 32",
            "384, 48",
            "521, 66"
    })
    void testWhenValidEcCoordinate_thenReturnFixedLengthEncoded(int fieldSize, int expectedByteLength) {
        // makes regular Base64 use '+' or '/', exposing use of the wrong encoder.
        // It is also short enough to verify left-padding to the curve size.
        // FB FF becomes +/8= in Base64 and -_8 in Base64URL
        var coordinate = new BigInteger("FBFF", 16);

        var result = JwkUtils.encodeEcCoordinate(coordinate, fieldSize);
        var decoded = Base64.getUrlDecoder().decode(result);

        assertEquals(expectedByteLength, decoded.length);
        assertEquals(coordinate, new BigInteger(1, decoded));
        assertFalse(result.contains("="));
    }

    @Test
    void testWhenEcCoordinateNegative_thenThrow() {
        var expectedMessage = "EC coordinate cannot be negative";
        var exception = assertThrows(
                JwkEncodingException.class,
                () -> JwkUtils.encodeEcCoordinate(BigInteger.valueOf(-1L), 256)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
