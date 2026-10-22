package janus.auth.jose.jwk.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SigningKeyManagerTest {

    private static final String RSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String P256_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String P384_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA384withECDSA";
    private static final String P521_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA512withECDSA";

    @Mock
    private Pkcs12SigningKeyStore signingKeyStoreMock;
    @Mock
    private X509CertificateIssuer certificateIssuerMock;
    @Mock
    private X509Certificate rsaCertificateMock;
    @Mock
    private X509Certificate p256CertificateMock;
    @Mock
    private X509Certificate p384CertificateMock;
    @Mock
    private X509Certificate p521CertificateMock;

    private SigningKeyManager signingKeyManager;

    @BeforeEach
    void setUp() {
        signingKeyManager = new SigningKeyManager(
                signingKeyStoreMock,
                certificateIssuerMock
        );
    }

    @Test
    void testWhenSigningKeysDoNotExist_thenGenerateAndStoreAll() {
        when(signingKeyStoreMock.containsAlias(anyString())).thenReturn(false);
        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(RSA_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(rsaCertificateMock);
        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(P256_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(p256CertificateMock);
        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(P384_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(p384CertificateMock);
        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(P521_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(p521CertificateMock);

        signingKeyManager.prepareSigningKeys();

        verifyAllAliasesChecked();
        verifyRsaKeyPrepared(
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS,
                rsaCertificateMock
        );
        verifyEcKeyPrepared(
                Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS,
                P256_CERTIFICATE_SIGNATURE_ALGORITHM,
                p256CertificateMock,
                256
        );
        verifyEcKeyPrepared(
                Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS,
                P384_CERTIFICATE_SIGNATURE_ALGORITHM,
                p384CertificateMock,
                384
        );
        verifyEcKeyPrepared(
                Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS,
                P521_CERTIFICATE_SIGNATURE_ALGORITHM,
                p521CertificateMock,
                521
        );
    }

    @Test
    void testWhenSigningKeysExist_thenDoNotIssueOrStore() {
        when(signingKeyStoreMock.containsAlias(anyString())).thenReturn(true);

        signingKeyManager.prepareSigningKeys();

        verifyAllAliasesChecked();
        verifyNoInteractions(certificateIssuerMock);
        verify(signingKeyStoreMock, never()).storePrivateKey(
                anyString(),
                any(PrivateKey.class),
                any(X509Certificate.class)
        );
    }

    @Test
    void testWhenSomeSigningKeysExist_thenGenerateAndStoreOnlyMissing() {
        when(signingKeyStoreMock.containsAlias(
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(true);
        when(signingKeyStoreMock.containsAlias(
                Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(false);
        when(signingKeyStoreMock.containsAlias(
                Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(true);
        when(signingKeyStoreMock.containsAlias(
                Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS
        )).thenReturn(false);

        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(P256_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(p256CertificateMock);
        when(certificateIssuerMock.issueSelfSigned(any(KeyPair.class),
                eq(P521_CERTIFICATE_SIGNATURE_ALGORITHM))).thenReturn(p521CertificateMock);

        signingKeyManager.prepareSigningKeys();

        verifyAllAliasesChecked();
        verifyEcKeyPrepared(
                Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS,
                P256_CERTIFICATE_SIGNATURE_ALGORITHM,
                p256CertificateMock,
                256
        );
        verifyEcKeyPrepared(
                Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS,
                P521_CERTIFICATE_SIGNATURE_ALGORITHM,
                p521CertificateMock,
                521
        );

        verify(certificateIssuerMock, never()).issueSelfSigned(
                any(KeyPair.class),
                eq(RSA_CERTIFICATE_SIGNATURE_ALGORITHM)
        );
        verify(certificateIssuerMock, never()).issueSelfSigned(
                any(KeyPair.class),
                eq(P384_CERTIFICATE_SIGNATURE_ALGORITHM)
        );
        verify(signingKeyStoreMock, never()).storePrivateKey(
                eq(Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS),
                any(PrivateKey.class),
                any(X509Certificate.class)
        );
        verify(signingKeyStoreMock, never()).storePrivateKey(
                eq(Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS),
                any(PrivateKey.class),
                any(X509Certificate.class)
        );
    }

    private void verifyAllAliasesChecked() {
        verify(signingKeyStoreMock).containsAlias(
                Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS
        );
        verify(signingKeyStoreMock).containsAlias(
                Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS
        );
        verify(signingKeyStoreMock).containsAlias(
                Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS
        );
        verify(signingKeyStoreMock).containsAlias(
                Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS
        );
    }

    private void verifyRsaKeyPrepared(String alias, X509Certificate certificate) {
        var keyPairCaptor = ArgumentCaptor.forClass(KeyPair.class);
        verify(certificateIssuerMock).issueSelfSigned(
                keyPairCaptor.capture(),
                eq(RSA_CERTIFICATE_SIGNATURE_ALGORITHM)
        );

        var privateKey = assertInstanceOf(
                RSAPrivateKey.class,
                keyPairCaptor.getValue().getPrivate()
        );
        verify(signingKeyStoreMock).storePrivateKey(
                eq(alias),
                same(privateKey),
                same(certificate)
        );
    }

    private void verifyEcKeyPrepared(
            String alias,
            String signatureAlgorithm,
            X509Certificate certificate,
            int expectedFieldSize
    ) {
        var keyPairCaptor = ArgumentCaptor.forClass(KeyPair.class);
        verify(certificateIssuerMock).issueSelfSigned(
                keyPairCaptor.capture(),
                eq(signatureAlgorithm)
        );

        var privateKey = assertInstanceOf(
                ECPrivateKey.class,
                keyPairCaptor.getValue().getPrivate()
        );
        assertEquals(
                expectedFieldSize,
                privateKey.getParams().getCurve().getField().getFieldSize()
        );
        verify(signingKeyStoreMock).storePrivateKey(
                eq(alias),
                same(privateKey),
                same(certificate)
        );
    }
}
