package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.type.Curve;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SigningKeyPairGeneratorTest {

    private static final byte[] TEST_PAYLOAD = "test-payload".getBytes(StandardCharsets.UTF_8);

    @Test
    void testWhenRsaKeyPairGenerated_thenReturnValidRsaKeyPair() throws GeneralSecurityException {
        var keyPair = SigningKeyPairGenerator.generateRsaKeyPair();

        var publicKey = assertInstanceOf(RSAPublicKey.class, keyPair.getPublic());
        assertInstanceOf(RSAPrivateKey.class, keyPair.getPrivate());

        assertEquals(SigningKeyPairGenerator.RSA_KEY_SIZE, publicKey.getModulus().bitLength());
        assertEquals(
                BigInteger.valueOf(SigningKeyPairGenerator.RSA_PUBLIC_EXPONENT),
                publicKey.getPublicExponent()
        );
        assertKeyPairCanSignAndVerify(keyPair, "SHA256withRSA");
    }

    @ParameterizedTest
    @MethodSource("validEcKeyPairInputs")
    void testWhenEcKeyPairGenerated_thenReturnValidEcKeyPair(
            Curve curve,
            int expectedFieldSize,
            String signatureAlgorithm
    ) throws GeneralSecurityException {
        var keyPair = SigningKeyPairGenerator.generateEcKeyPair(curve);

        var publicKey = assertInstanceOf(ECPublicKey.class, keyPair.getPublic());
        var privateKey = assertInstanceOf(ECPrivateKey.class, keyPair.getPrivate());

        assertEquals(
                expectedFieldSize,
                publicKey.getParams().getCurve().getField().getFieldSize()
        );
        assertEquals(
                expectedFieldSize,
                privateKey.getParams().getCurve().getField().getFieldSize()
        );
        assertKeyPairCanSignAndVerify(keyPair, signatureAlgorithm);
    }

    private static Stream<Arguments> validEcKeyPairInputs() {
        return Stream.of(
                Arguments.of(Curve.P256, 256, "SHA256withECDSA"),
                Arguments.of(Curve.P384, 384, "SHA384withECDSA"),
                Arguments.of(Curve.P521, 521, "SHA512withECDSA")
        );
    }

    private static void assertKeyPairCanSignAndVerify(KeyPair keyPair, String signatureAlgorithm)
            throws GeneralSecurityException {
        var signer = Signature.getInstance(signatureAlgorithm);
        signer.initSign(keyPair.getPrivate());
        signer.update(TEST_PAYLOAD);
        var signature = signer.sign();

        var verifier = Signature.getInstance(signatureAlgorithm);
        verifier.initVerify(keyPair.getPublic());
        verifier.update(TEST_PAYLOAD);

        assertTrue(verifier.verify(signature));
    }
}
