package janus.auth.jose.jwk.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.RSAPublicJwk;
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

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwksServiceTest {

    private static final Set<KeyOperation> VERIFY_KEY_OPERATIONS =
            Set.of(KeyOperation.VERIFY);

    @Mock
    private Pkcs12SigningKeyStore signingKeyStoreMock;
    @Mock
    private PublicJwkMapper publicJwkMapperMock;

    @Mock
    private X509Certificate rsaCertificateMock;
    @Mock
    private X509Certificate p256CertificateMock;
    @Mock
    private X509Certificate p384CertificateMock;
    @Mock
    private X509Certificate p521CertificateMock;

    @Mock
    private RSAPublicJwk rsaPublicJwkMock;
    @Mock
    private ECPublicJwk p256PublicJwkMock;
    @Mock
    private ECPublicJwk p384PublicJwkMock;
    @Mock
    private ECPublicJwk p521PublicJwkMock;

    @Mock
    private X509Certificate activeEcCertificateMock;
    @Mock
    private ECPublicJwk activeEcPublicJwkMock;

    private JwksService jwksService;

    @BeforeEach
    void setUp() {
        jwksService = new JwksService(signingKeyStoreMock, publicJwkMapperMock);
    }

    @Test
    void testWhenPublicJwkSetRequested_thenReturnAllPublicSigningKeys() {
        when(signingKeyStoreMock.getCertificate(Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS))
                .thenReturn(rsaCertificateMock);
        when(signingKeyStoreMock.getCertificate(Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS))
                .thenReturn(p256CertificateMock);
        when(signingKeyStoreMock.getCertificate(Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS))
                .thenReturn(p384CertificateMock);
        when(signingKeyStoreMock.getCertificate(Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS))
                .thenReturn(p521CertificateMock);

        when(publicJwkMapperMock.toPublicRSA(
                rsaCertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                Algorithm.RS256,
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(rsaPublicJwkMock);
        when(publicJwkMapperMock.toPublicEC(
                p256CertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                Algorithm.ES256,
                Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(p256PublicJwkMock);
        when(publicJwkMapperMock.toPublicEC(
                p384CertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                Algorithm.ES384,
                Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(p384PublicJwkMock);
        when(publicJwkMapperMock.toPublicEC(
                p521CertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                Algorithm.ES512,
                Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(p521PublicJwkMock);

        var result = jwksService.getPublicJwkSet();

        assertEquals(
                List.of(rsaPublicJwkMock, p256PublicJwkMock, p384PublicJwkMock, p521PublicJwkMock),
                result.keys()
        );
        assertTrue(result.additionalProperties().isEmpty());
    }

    @Test
    void testWhenRsaSigningJwkRequested_thenReturnRsaPublicJwk() {
        when(signingKeyStoreMock.getCertificate(
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(rsaCertificateMock);
        when(publicJwkMapperMock.toPublicRSA(
                rsaCertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                Algorithm.RS256,
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(rsaPublicJwkMock);

        var result = jwksService.getActivePublicSigningJwk(Algorithm.RS256);

        assertSame(rsaPublicJwkMock, result);
    }

    @ParameterizedTest
    @MethodSource("validEcSigningJwkInputs")
    void testWhenEcSigningJwkRequested_thenReturnMatchingEcPublicJwk(
            Algorithm algorithm,
            String alias
    ) {
        when(signingKeyStoreMock.getCertificate(alias))
                .thenReturn(activeEcCertificateMock);
        when(publicJwkMapperMock.toPublicEC(
                activeEcCertificateMock,
                PublicKeyUse.SIG,
                VERIFY_KEY_OPERATIONS,
                algorithm,
                alias
        )).thenReturn(activeEcPublicJwkMock);

        var result = jwksService.getActivePublicSigningJwk(algorithm);

        assertSame(activeEcPublicJwkMock, result);
    }

    private static Stream<Arguments> validEcSigningJwkInputs() {
        return Stream.of(
                Arguments.of(
                        Algorithm.ES256,
                        Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS
                ),
                Arguments.of(
                        Algorithm.ES384,
                        Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS
                ),
                Arguments.of(
                        Algorithm.ES512,
                        Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS
                )
        );
    }
}
