package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.exception.CertificateIssuanceException;
import janus.auth.jose.jwk.domain.type.Curve;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.security.auth.x500.X500Principal;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class X509CertificateIssuerTest {

    private static final String EXPECTED_DISTINGUISHED_NAME = "C=HR, CN=Janus Self-Signed Certificate";
    private static final Duration EXPECTED_VALIDITY = Duration.ofDays(3650).plusMinutes(5);
    private static final int DIGITAL_SIGNATURE_KEY_USAGE_INDEX = 0;

    @ParameterizedTest
    @MethodSource("validCertificateInputs")
    void testWhenCertificateInputsValid_thenReturnValidSelfSignedCertificate(
            KeyPair keyPair, String signatureAlgorithm) throws GeneralSecurityException {
        var certificateIssuer = new X509CertificateIssuer();
        var expectedPrincipal = new X500Principal(EXPECTED_DISTINGUISHED_NAME);

        var certificate = certificateIssuer.issueSelfSigned(keyPair, signatureAlgorithm);

        assertEquals(expectedPrincipal, certificate.getSubjectX500Principal());
        assertEquals(certificate.getSubjectX500Principal(), certificate.getIssuerX500Principal());
        assertArrayEquals(keyPair.getPublic().getEncoded(), certificate.getPublicKey().getEncoded());

        assertEquals(1, certificate.getSerialNumber().signum());
        assertEquals(128, certificate.getSerialNumber().bitLength());
        assertEquals(
                EXPECTED_VALIDITY,
                Duration.between(
                        certificate.getNotBefore().toInstant(),
                        certificate.getNotAfter().toInstant()
                )
        );
        certificate.checkValidity();

        assertEquals(signatureAlgorithm, certificate.getSigAlgName());
        certificate.verify(keyPair.getPublic());

        assertEquals(-1, certificate.getBasicConstraints());
        var keyUsage = certificate.getKeyUsage();
        assertNotNull(keyUsage);
        assertTrue(keyUsage[DIGITAL_SIGNATURE_KEY_USAGE_INDEX]);

        assertTrue(certificate.getCriticalExtensionOIDs().contains(Extension.basicConstraints.getId()));
        assertTrue(certificate.getCriticalExtensionOIDs().contains(Extension.keyUsage.getId()));
        assertTrue(certificate.getNonCriticalExtensionOIDs().contains(Extension.subjectKeyIdentifier.getId()));
        assertTrue(certificate.getNonCriticalExtensionOIDs().contains(Extension.authorityKeyIdentifier.getId()));
    }

    private static Stream<Arguments> validCertificateInputs() {
        return Stream.of(
                Arguments.of(SigningKeyPairGenerator.generateRsaKeyPair(), "SHA256withRSA"),
                Arguments.of(SigningKeyPairGenerator.generateEcKeyPair(Curve.P256), "SHA256withECDSA"),
                Arguments.of(SigningKeyPairGenerator.generateEcKeyPair(Curve.P384), "SHA384withECDSA"),
                Arguments.of(SigningKeyPairGenerator.generateEcKeyPair(Curve.P521), "SHA512withECDSA")
        );
    }

    @Test
    void testWhenPrivateKeyNotCompatibleWithSignatureAlgorithm_thenWrapException() {
        var certificateIssuer = new X509CertificateIssuer();
        var keyPair = SigningKeyPairGenerator.generateEcKeyPair(Curve.P256);
        var expectedMessage = "Could not issue certificate";

        var exception = assertThrows(
                CertificateIssuanceException.class,
                () -> certificateIssuer.issueSelfSigned(
                        keyPair,
                        "SHA256withRSA"
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(OperatorCreationException.class, exception.getCause());
    }
}
