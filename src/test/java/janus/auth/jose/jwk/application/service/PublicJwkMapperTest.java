package janus.auth.jose.jwk.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.common.JwkUtils;
import janus.auth.jose.jwk.domain.exception.PublicJwkMappingException;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import janus.auth.jose.jwk.domain.type.Curve;
import janus.auth.jose.jwk.domain.type.KeyOperation;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublicJwkMapperTest {

    private static final String X5U_BASE_URL = "https://janus.example.com/x509";
    private static final String TEST_KID = "kid1";
    private static final Set<KeyOperation> TEST_KEY_OPERATIONS = Set.of(KeyOperation.VERIFY);
    private static final byte[] CERTIFICATE_DER = "abc".getBytes(StandardCharsets.US_ASCII);
    private static final String ENCODED_CERTIFICATE = "YWJj";
    private static final String SHA_1_CERTIFICATE_THUMBPRINT = "qZk-NkcGgWq6PiVxeFDCbJzQ2J0";
    private static final String SHA_256_CERTIFICATE_THUMBPRINT = "ungWv48Bz-pBQUDeXa4iI7ADYaOWF3qctBD_YfIAFa0";
    private static final BigInteger RSA_MODULUS = BigInteger.valueOf(255);
    private static final BigInteger RSA_PUBLIC_EXPONENT = BigInteger.valueOf(65537);
    private static final BigInteger EC_X_COORDINATE = BigInteger.valueOf(255);
    private static final BigInteger EC_Y_COORDINATE = BigInteger.valueOf(128);

    @Mock
    private X509Certificate certificateMock;
    @Mock
    private RSAPublicKey rsaPublicKeyMock;
    @Mock
    private ECPublicKey ecPublicKeyMock;
    @Mock
    private ECParameterSpec ecParameterSpecMock;
    @Mock
    private EllipticCurve ellipticCurveMock;
    @Mock
    private ECField ecFieldMock;

    private PublicJwkMapper publicJwkMapper;

    @BeforeEach
    void setUp() {
        publicJwkMapper = new PublicJwkMapper(X5U_BASE_URL);
    }

    @Test
    void testWhenRsaCertificateValid_thenReturnRsaPublicJwk()
            throws CertificateEncodingException {
        when(certificateMock.getPublicKey()).thenReturn(rsaPublicKeyMock);
        when(certificateMock.getEncoded()).thenReturn(CERTIFICATE_DER);
        when(rsaPublicKeyMock.getModulus()).thenReturn(RSA_MODULUS);
        when(rsaPublicKeyMock.getPublicExponent()).thenReturn(RSA_PUBLIC_EXPONENT);

        var expected = new RSAPublicJwk(
                PublicKeyUse.SIG,
                TEST_KEY_OPERATIONS,
                Algorithm.RS256,
                TEST_KID,
                X5U_BASE_URL + "/" + TEST_KID + ".pem",
                List.of(ENCODED_CERTIFICATE),
                SHA_1_CERTIFICATE_THUMBPRINT,
                SHA_256_CERTIFICATE_THUMBPRINT,
                "_w",
                "AQAB"
        );

        var result = publicJwkMapper.toPublicRSA(
                certificateMock,
                PublicKeyUse.SIG,
                TEST_KEY_OPERATIONS,
                Algorithm.RS256,
                TEST_KID
        );

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("validEcMappings")
    void testWhenEcCertificateValid_thenReturnEcPublicJwk(
            int fieldSize,
            Curve expectedCurve,
            Algorithm algorithm
    ) throws CertificateEncodingException {
        stubEcPublicKey(fieldSize);
        when(certificateMock.getEncoded()).thenReturn(CERTIFICATE_DER);
        when(ecPublicKeyMock.getW()).thenReturn(
                new ECPoint(EC_X_COORDINATE, EC_Y_COORDINATE)
        );

        var expected = new ECPublicJwk(
                PublicKeyUse.SIG,
                TEST_KEY_OPERATIONS,
                algorithm,
                TEST_KID,
                X5U_BASE_URL + "/" + TEST_KID + ".pem",
                List.of(ENCODED_CERTIFICATE),
                SHA_1_CERTIFICATE_THUMBPRINT,
                SHA_256_CERTIFICATE_THUMBPRINT,
                expectedCurve,
                JwkUtils.encodeEcCoordinate(EC_X_COORDINATE, fieldSize),
                JwkUtils.encodeEcCoordinate(EC_Y_COORDINATE, fieldSize)
        );

        var result = publicJwkMapper.toPublicEC(
                certificateMock,
                PublicKeyUse.SIG,
                TEST_KEY_OPERATIONS,
                algorithm,
                TEST_KID
        );

        assertEquals(expected, result);
    }

    private static Stream<Arguments> validEcMappings() {
        return Stream.of(
                Arguments.of(256, Curve.P256, Algorithm.ES256),
                Arguments.of(384, Curve.P384, Algorithm.ES384),
                Arguments.of(521, Curve.P521, Algorithm.ES512)
        );
    }

    @Test
    void testWhenRsaMappingReceivesNonRsaKey_thenThrow() {
        when(certificateMock.getPublicKey()).thenReturn(ecPublicKeyMock);
        var expectedMessage = "Certificate does not contain an RSA public key";

        var exception = assertThrows(
                PublicJwkMappingException.class,
                () -> publicJwkMapper.toPublicRSA(
                        certificateMock,
                        PublicKeyUse.SIG,
                        TEST_KEY_OPERATIONS,
                        Algorithm.RS256,
                        TEST_KID
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenEcMappingReceivesNonEcKey_thenThrow() {
        when(certificateMock.getPublicKey()).thenReturn(rsaPublicKeyMock);
        var expectedMessage = "Certificate does not contain an EC public key";

        var exception = assertThrows(
                PublicJwkMappingException.class,
                () -> publicJwkMapper.toPublicEC(
                        certificateMock,
                        PublicKeyUse.SIG,
                        TEST_KEY_OPERATIONS,
                        Algorithm.ES256,
                        TEST_KID
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenEcFieldSizeUnsupported_thenThrow() {
        stubEcPublicKey(255);
        var expectedMessage = "Unsupported EC field size: 255";

        var exception = assertThrows(
                PublicJwkMappingException.class,
                () -> publicJwkMapper.toPublicEC(
                        certificateMock,
                        PublicKeyUse.SIG,
                        TEST_KEY_OPERATIONS,
                        Algorithm.ES256,
                        TEST_KID
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private void stubEcPublicKey(int fieldSize) {
        when(certificateMock.getPublicKey()).thenReturn(ecPublicKeyMock);
        when(ecPublicKeyMock.getParams()).thenReturn(ecParameterSpecMock);
        when(ecParameterSpecMock.getCurve()).thenReturn(ellipticCurveMock);
        when(ellipticCurveMock.getField()).thenReturn(ecFieldMock);
        when(ecFieldMock.getFieldSize()).thenReturn(fieldSize);
    }
}
