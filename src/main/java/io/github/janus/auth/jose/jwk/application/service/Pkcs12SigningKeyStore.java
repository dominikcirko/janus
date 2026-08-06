package io.github.janus.auth.jose.jwk.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Owns access to Janus's persistent signing-key store.
 *
 * <p>The store keeps private signing keys and their certificates out of the
 * public JWK model. Signing code can obtain a private key by alias, while the
 * JWKS workflow reads only the corresponding certificate and public key.</p>
 */
@Component
public class Pkcs12SigningKeyStore {

    public static final String RSA_SIGNING_KEYSTORE_ALIAS = "janus-signing-rsa-1";
    public static final String EC_P256_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p256-1";
    public static final String EC_P384_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p384-1";
    public static final String EC_P521_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p521-1";

    private static final String KEYSTORE_TYPE = "PKCS12";

    @Value("${janus.signing.keystore.password}")
    private String keyStorePassword;

    @Value("${janus.signing.keystore.path}")
    private String signingKeystorePath;

    private KeyStore keyStore;

    /**
     * Loads the configured PKCS#12 file, or initializes an empty store when
     * the file does not exist yet. Calling this method more than once is safe.
     */
    public synchronized void initialize() {
        if (keyStore != null) {
            return;
        }

        try {
            KeyStore loadedKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            File file = new File(signingKeystorePath);
            char[] charKeyStorePassword = keyStorePassword.toCharArray();

            if (Files.isRegularFile(Path.of(signingKeystorePath))) {
                loadKeyStore(file, loadedKeyStore, charKeyStorePassword);
            } else {
                initializeEmptyKeyStore(loadedKeyStore, charKeyStorePassword);
            }

            keyStore = loadedKeyStore;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Could not initialize signing keystore: " + signingKeystorePath,
                    e
            );
        }
    }

    public synchronized boolean containsAlias(String alias) {
        try {
            return initializedKeyStore().containsAlias(alias);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not inspect signing keystore", e);
        }
    }

    /**
     * Stores a private signing key with the certificate required by a Java
     * private-key entry, then persists the complete PKCS#12 store to disk.
     */
    public synchronized void storePrivateKey(
            String alias,
            PrivateKey privateKey,
            X509Certificate certificate
    ) {
        try {
            KeyStore initializedKeyStore = initializedKeyStore();
            char[] charKeyStorePassword = keyStorePassword.toCharArray();
            initializedKeyStore.setKeyEntry(
                    alias,
                    privateKey,
                    charKeyStorePassword,
                    new Certificate[]{certificate}
            );
            storeKeyStore(
                    new File(signingKeystorePath),
                    initializedKeyStore,
                    charKeyStorePassword
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not store signing key: " + alias, e);
        }
    }

    /**
     * Returns the certificate whose public key can be mapped to a published
     * JWK. Private key material is not exposed by this operation.
     */
    public synchronized X509Certificate getCertificate(String alias) {
        try {
            Certificate certificate = initializedKeyStore().getCertificate(alias);
            if (certificate == null) {
                throw new IllegalStateException("No certificate found for signing key: " + alias);
            }
            if (!(certificate instanceof X509Certificate x509Certificate)) {
                throw new IllegalStateException("Certificate is not X.509: " + alias);
            }
            return x509Certificate;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not read signing certificate: " + alias, e);
        }
    }

    /** Returns the private key used by the token-signing workflow. */
    public synchronized PrivateKey getPrivateKey(String alias) {
        try {
            Key key = initializedKeyStore().getKey(alias, keyStorePassword.toCharArray());
            if (key == null) {
                throw new IllegalStateException("No private key found for alias: " + alias);
            }
            if (!(key instanceof PrivateKey privateKey)) {
                throw new IllegalStateException("Entry is not a private key: " + alias);
            }
            return privateKey;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not read private signing key: " + alias, e);
        }
    }

    private KeyStore initializedKeyStore() {
        initialize();
        return keyStore;
    }

    /**
     * Loads an existing PKCS#12 file into the supplied {@link KeyStore}.
     *
     * @param file the existing keystore file
     * @param keyStore the keystore instance to populate
     * @param charKeyStorePassword the password used to open the file
     */
    private void loadKeyStore(
            File file,
            KeyStore keyStore,
            char[] charKeyStorePassword
    ) {
        try (InputStream keyStoreData = new FileInputStream(file)) {
            keyStore.load(keyStoreData, charKeyStorePassword);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the supplied {@link KeyStore} as empty so entries can be
     * added to it. This does not create a file on disk.
     *
     * @param keyStore the keystore instance to initialize
     * @param charKeyStorePassword the password that will protect the store
     */
    private void initializeEmptyKeyStore(
            KeyStore keyStore,
            char[] charKeyStorePassword
    ) {
        try {
            keyStore.load(null, charKeyStorePassword);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the current {@link KeyStore} contents to disk. The file is
     * created when missing and overwritten when it already exists.
     *
     * @param file the keystore file to create or overwrite
     * @param keyStore the keystore whose contents will be written
     * @param charKeyStorePassword the password used to protect the file
     */
    private void storeKeyStore(
            File file,
            KeyStore keyStore,
            char[] charKeyStorePassword
    ) {
        if (file.isDirectory()) {
            throw new RuntimeException("Must be a file");
        }

        try (FileOutputStream keyStoreOutputStream = new FileOutputStream(file)) {
            keyStore.store(keyStoreOutputStream, charKeyStorePassword);
        } catch (CertificateException |
                 KeyStoreException |
                 NoSuchAlgorithmException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }
}
