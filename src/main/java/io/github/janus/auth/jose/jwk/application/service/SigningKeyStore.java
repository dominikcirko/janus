package io.github.janus.auth.jose.jwk.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Owns access to Janus's persistent signing-key store.
 *
 * <p>The store keeps private signing keys and their certificates out of the
 * public JWK model. Signing code can obtain a private key by alias, while the
 * JWKS workflow reads only the corresponding certificate and public key.</p>
 */
@Component
@ConditionalOnProperty(
        prefix = "janus.signing.keystore",
        name = {"path", "password"}
)
public class SigningKeyStore {

    public static final String RSA_SIGNING_KEYSTORE_ALIAS = "janus-signing-rsa-1";
    public static final String EC_P256_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p256-1";
    public static final String EC_P384_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p384-1";
    public static final String EC_P521_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p521-1";

    private static final String KEYSTORE_TYPE = "PKCS12";

    private final Path path;
    private final char[] password;
    private KeyStore keyStore;

    public SigningKeyStore(
            @Value("${janus.signing.keystore.path}") String path,
            @Value("${janus.signing.keystore.password}") String password
    ) {
        this.path = Path.of(path).toAbsolutePath().normalize();
        this.password = password.toCharArray();
    }

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

            if (Files.isRegularFile(path)) {
                try (InputStream input = Files.newInputStream(path)) {
                    loadedKeyStore.load(input, password);
                }
            } else if (Files.exists(path)) {
                throw new IllegalStateException("Keystore path is not a regular file: " + path);
            } else {
                loadedKeyStore.load(null, password);
            }

            keyStore = loadedKeyStore;
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Could not initialize signing keystore: " + path, e);
        }
    }

    /** Returns whether a stored key or certificate uses the given alias. */
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
            initializedKeyStore.setKeyEntry(
                    alias,
                    privateKey,
                    password,
                    new Certificate[]{certificate}
            );
            persist(initializedKeyStore);
        } catch (GeneralSecurityException | IOException e) {
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
            Key key = initializedKeyStore().getKey(alias, password);
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

    private void persist(KeyStore initializedKeyStore)
            throws IOException, GeneralSecurityException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (OutputStream output = Files.newOutputStream(path)) {
            initializedKeyStore.store(output, password);
        }
    }
}
