package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.exception.SigningKeyStoreException;
import janus.auth.jose.jwk.domain.type.Curve;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

public class Pkcs12SigningKeyStoreTest {

    private static final String TEST_PASSWORD = "test-password";
    private static final String TEST_ALIAS = "test-signing-key";

    private static KeyPair keyPair;
    private static X509Certificate certificate;

    @TempDir
    private Path tempDirectory;

    private Path keyStorePath;
    private Pkcs12SigningKeyStore signingKeyStore;

    @BeforeAll
    static void createSigningKeyFixture() {
        keyPair = SigningKeyPairGenerator.generateEcKeyPair(Curve.P256);
        certificate = new X509CertificateIssuer().issueSelfSigned(
                keyPair,
                "SHA256withECDSA"
        );
    }

    @BeforeEach
    void setUp() {
        keyStorePath = tempDirectory.resolve("signing-keys.p12");
        signingKeyStore = new Pkcs12SigningKeyStore(
                TEST_PASSWORD,
                keyStorePath.toString()
        );
    }

    @Test
    void testWhenKeyStoreEmpty_thenAliasAndCertificateNotFound() {
        assertFalse(signingKeyStore.containsAlias(TEST_ALIAS));
        assertTrue(signingKeyStore.findCertificate(TEST_ALIAS).isEmpty());
    }

    @Test
    void testWhenPrivateKeyStored_thenPersistAndReloadEntry()
            throws CertificateEncodingException {
        signingKeyStore.storePrivateKey(
                TEST_ALIAS,
                keyPair.getPrivate(),
                certificate
        );

        var reloadedKeyStore = new Pkcs12SigningKeyStore(
                TEST_PASSWORD,
                keyStorePath.toString()
        );

        assertTrue(reloadedKeyStore.containsAlias(TEST_ALIAS));
        assertArrayEquals(
                keyPair.getPrivate().getEncoded(),
                reloadedKeyStore.getPrivateKey(TEST_ALIAS).getEncoded()
        );
        assertArrayEquals(
                certificate.getEncoded(),
                reloadedKeyStore.getCertificate(TEST_ALIAS).getEncoded()
        );
    }

    @Test
    void testWhenAliasAlreadyExists_thenThrow() {
        signingKeyStore.storePrivateKey(
                TEST_ALIAS,
                keyPair.getPrivate(),
                certificate
        );
        var expectedMessage = "Key already exists: " + TEST_ALIAS;

        var exception = assertThrows(
                SigningKeyStoreException.class,
                () -> signingKeyStore.storePrivateKey(
                        TEST_ALIAS,
                        keyPair.getPrivate(),
                        certificate
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenCertificateMissing_thenThrow() {
        var expectedMessage = "Signing certificate not found: " + TEST_ALIAS;

        var exception = assertThrows(
                SigningKeyStoreException.class,
                () -> signingKeyStore.getCertificate(TEST_ALIAS)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenPrivateKeyMissing_thenThrow() {
        var expectedMessage = "Entry is not a private key: " + TEST_ALIAS;

        var exception = assertThrows(
                SigningKeyStoreException.class,
                () -> signingKeyStore.getPrivateKey(TEST_ALIAS)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testWhenKeyStorePasswordIncorrect_thenWrapLoadException() {
        signingKeyStore.storePrivateKey(
                TEST_ALIAS,
                keyPair.getPrivate(),
                certificate
        );
        var keyStoreWithWrongPassword = new Pkcs12SigningKeyStore(
                "wrong-password",
                keyStorePath.toString()
        );
        var expectedMessage = "Could not load signing keystore";

        var exception = assertThrows(
                SigningKeyStoreException.class,
                () -> keyStoreWithWrongPassword.containsAlias(TEST_ALIAS)
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void testWhenKeyStorePathIsDirectory_thenThrow() {
        var keyStoreWithDirectoryPath = new Pkcs12SigningKeyStore(
                TEST_PASSWORD,
                tempDirectory.toString()
        );
        var expectedMessage = "Signing keystore path must be a file";

        var exception = assertThrows(
                SigningKeyStoreException.class,
                () -> keyStoreWithDirectoryPath.storePrivateKey(
                        TEST_ALIAS,
                        keyPair.getPrivate(),
                        certificate
                )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
