package janus.auth.jose.jws.application;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseCommonParams;
import janus.auth.jose.jwk.application.service.SigningKeyPairGenerator;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import janus.auth.jose.jws.domain.exception.JwsSigningException;
import janus.auth.jose.jws.domain.model.JwsHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.ECField;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwsSignerTest {

    @Mock
    private JwsHeader headerMock;
    @Mock
    private JoseCommonParams paramsMock;
    @Mock
    private ECPublicJwk ecJwkMock;
    @Mock
    private RSAPublicJwk rsaJwkMock;
    @Mock
    private PrivateKey privateKeyMock;

    private static final byte[] TEST_PAYLOAD = "test-payload".getBytes(StandardCharsets.UTF_8);
    private static final int INVALID_CURVE_SIZE = 123;
    private static final BigInteger UNDERSIZED_RSA_MODULUS = BigInteger.valueOf(3233);
    private static final BigInteger UNDERSIZED_RSA_PRIVATE_EXPONENT = BigInteger.valueOf(2753);

    @Test
    void testWhenRsaSigningInputsAreValid_thenReturnValidCompactJws()
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        var signer = new JwsSigner(new ObjectMapper());
        var keypair = SigningKeyPairGenerator.generateRsaKeyPair();
        var decoder = Base64.getUrlDecoder();

        when(rsaJwkMock.kid()).thenReturn("kid1");
        when(rsaJwkMock.alg()).thenReturn(Algorithm.RS256);

        var params = JoseCommonParams.builder()
                .kid("kid1")
                .build();

        var header = JwsHeader.builder()
                .alg(Algorithm.RS256)
                .params(params)
                .build();

        var signed = signer.sign(header, TEST_PAYLOAD, keypair.getPrivate(), rsaJwkMock);

        String[] jwsSegments = signed.split("[.]");
        var decodedHeader = decoder.decode(jwsSegments[0]);
        var decodedPayload = decoder.decode(jwsSegments[1]);
        var decodedSignature = decoder.decode(jwsSegments[2]);

        var signingInput = jwsSegments[0] + "." + jwsSegments[1];

        var verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(keypair.getPublic());
        verifier.update(signingInput.getBytes(StandardCharsets.US_ASCII));

        var objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(decodedHeader);

        assertEquals(3, jwsSegments.length);
        assertFalse(jwsSegments[0].isBlank());
        assertFalse(jwsSegments[1].isBlank());
        assertFalse(jwsSegments[2].isBlank());
        assertArrayEquals(TEST_PAYLOAD, decodedPayload);
        assertEquals("RS256", node.get("alg").asString());
        assertEquals("kid1", node.get("kid").asString());
        assertTrue(verifier.verify(decodedSignature));
    }

    @ParameterizedTest
    @MethodSource("validEcSigningInputs")
    void testWhenEcSigningInputsAreValid_thenReturnValidCompactJws(
            Algorithm algorithm,
            String curveName
    ) {

    }

    @ParameterizedTest
    @MethodSource("invalidSigningInputs")
    void testWhenSigningInputsNull_thenThrow(JwsHeader header, PublicJwk jwk, byte[] payload,
                                             PrivateKey privateKey, String expectedMessage){
        var signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class, () -> signer.sign(header, payload, privateKey, jwk)
        );
        assertEquals(expectedMessage, exception.getMessage());

    }

    private static Stream<Arguments> invalidSigningInputs(){
        JwsHeader header = mock(JwsHeader.class);
        JoseCommonParams params = mock(JoseCommonParams.class);
        PublicJwk jwk = mock(ECPublicJwk.class);
        PrivateKey privateKey = mock(PrivateKey.class);
        var expectedMessage = "Required JWS fields must not be null";

        when(header.params()).thenReturn(params);
        when(header.params().kid()).thenReturn(null);
        when(header.params()).thenReturn(null);
        when(header.alg()).thenReturn(null);
        when(jwk.kid()).thenReturn(null);
        when(jwk.alg()).thenReturn(null);

        return Stream.of(
            Arguments.of(null, jwk, TEST_PAYLOAD, privateKey, expectedMessage),
            Arguments.of(header, null, TEST_PAYLOAD, privateKey, expectedMessage),
            Arguments.of(header, jwk, null, privateKey, expectedMessage),
            Arguments.of(header, jwk, TEST_PAYLOAD, null, expectedMessage)
        );
    }

    @Test
    void testWhenHeaderKidNotEqualToJwkKid_thenThrow(){

        when(headerMock.params()).thenReturn(paramsMock);
        when(headerMock.alg()).thenReturn(Algorithm.ES256);
        when(headerMock.params().kid()).thenReturn("kid1");
        when(ecJwkMock.kid()).thenReturn("kid2");
        when(ecJwkMock.alg()).thenReturn(Algorithm.ES256);

        var expectedMessage = "JWS Header's kid must be identical to public JWK's kid";

        var signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, privateKeyMock, ecJwkMock));
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void testWhenHeaderAlgNotEqualToJwkAlg_thenThrow(){

        when(headerMock.params()).thenReturn(paramsMock);
        when(headerMock.params().kid()).thenReturn("kid1");
        when(ecJwkMock.kid()).thenReturn("kid1");
        when(ecJwkMock.alg()).thenReturn(Algorithm.ES384);
        when(headerMock.alg()).thenReturn(Algorithm.ES256);

        var expectedMessage = "JWS Header's alg must be identical to public JWK's alg";

        JwsSigner signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, privateKeyMock, ecJwkMock));
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void testWhenRsaPrivateKeyWithNonRsaAlgorithm_thenThrow(){
        var signer = new JwsSigner(new ObjectMapper());
        var rsaPrivateKey = mock(RSAPrivateKey.class);

        when(headerMock.params()).thenReturn(paramsMock);
        when(headerMock.params().kid()).thenReturn("kid1");
        when(ecJwkMock.kid()).thenReturn("kid1");
        when(ecJwkMock.alg()).thenReturn(Algorithm.ES256);
        when(headerMock.alg()).thenReturn(Algorithm.ES256);

        var expectedMessage = "ES256 requires an EC private key";

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, rsaPrivateKey, ecJwkMock)
        );
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void testWhenEcPrivateKeyWithNonEcAlgorithm_thenThrow(){
        var signer = new JwsSigner(new ObjectMapper());
        var ecPrivateKey = mock(ECPrivateKey.class);

        when(headerMock.params()).thenReturn(paramsMock);
        when(headerMock.params().kid()).thenReturn("kid1");
        when(ecJwkMock.kid()).thenReturn("kid1");
        when(ecJwkMock.alg()).thenReturn(Algorithm.RS256);
        when(headerMock.alg()).thenReturn(Algorithm.RS256);

        var expectedMessage = "RS256 requires an RSA private key";

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, ecPrivateKey, ecJwkMock)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenCurveFieldSizeNotValid_thenThrow(){
        var signer = new JwsSigner(new ObjectMapper());
        var ecPrivateKey = mock(ECPrivateKey.class);

        when(headerMock.params()).thenReturn(paramsMock);
        when(headerMock.params().kid()).thenReturn("kid1");
        when(ecJwkMock.kid()).thenReturn("kid1");
        when(ecJwkMock.alg()).thenReturn(Algorithm.ES256);
        when(headerMock.alg()).thenReturn(Algorithm.ES256);
        when(ecPrivateKey.getParams()).thenReturn(mock(ECParameterSpec.class));
        when(ecPrivateKey.getParams().getCurve()).thenReturn(mock(EllipticCurve.class));
        when(ecPrivateKey.getParams().getCurve().getField()).thenReturn(mock(ECField.class));
        when(ecPrivateKey.getParams().getCurve().getField().getFieldSize()).thenReturn(INVALID_CURVE_SIZE);

        var expectedMessage = "ES256 requires EC field size 256";

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, ecPrivateKey, ecJwkMock)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenJcaRejectsPrivateKey_thenWrapException() {
        var signer = new JwsSigner(new ObjectMapper());
        var invalidKey = mock(RSAPrivateKey.class);
        var rsaJwk = mock(RSAPublicJwk.class);

        // 12-bit RSA key: structurally plausible but rejected by the JDK as too small
        // -> minimum is 512 bits, source: Table 4-10 on
        // https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html#GUID-7093246A-31A3-4304-AC5F-5FB6400405E2

        // the goal is to trigger a GeneralSecurityException inside sign()'s try block
        // and verify that it gets wrapped in our custom JwsSigningException.
        // We chose to trigger an InvalidKeyException in initSign() by providing an invalid key.
        when(invalidKey.getAlgorithm()).thenReturn("RSA");
        when(invalidKey.getModulus()).thenReturn(UNDERSIZED_RSA_MODULUS);
        when(invalidKey.getPrivateExponent()).thenReturn(UNDERSIZED_RSA_PRIVATE_EXPONENT);

        when(headerMock.params()).thenReturn(paramsMock);
        when(paramsMock.kid()).thenReturn("kid1");
        when(headerMock.alg()).thenReturn(Algorithm.RS256);

        when(rsaJwk.kid()).thenReturn("kid1");
        when(rsaJwk.alg()).thenReturn(Algorithm.RS256);

        var expectedMessage = "Could not create JWS signature";

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(headerMock, TEST_PAYLOAD, invalidKey, rsaJwk)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(InvalidKeyException.class, exception.getCause());
    }

}
