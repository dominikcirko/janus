package janus.auth.jose.jwk.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


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

    public synchronized boolean containsAlias(String alias) {
        try {
            return getOrInitializeKeyStore().containsAlias(alias);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not inspect signing keystore", e);
        }
    }

    public synchronized X509Certificate getCertificate(String alias) {
        try {
            Certificate certificate = getOrInitializeKeyStore().getCertificate(alias);

            if (!(certificate instanceof X509Certificate x509Certificate)) {
                throw new IllegalStateException("Certificate is either null or not X.509: " + alias);
            }
            return x509Certificate;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not read signing certificate: " + alias, e);
        }
    }

    public synchronized PrivateKey getPrivateKey(String alias) {
        try {
            Key key = getOrInitializeKeyStore().getKey(alias, keyStorePassword.toCharArray());
            if (!(key instanceof PrivateKey privateKey)) {
                throw new IllegalStateException("Entry is not a private key: " + alias);
            }
            return privateKey;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not read private signing key: " + alias, e);
        }
    }

    public synchronized void storePrivateKey(
            String alias,
            PrivateKey privateKey,
            X509Certificate certificate
    ) {
        var pathToKeyStoreFile = Path.of(signingKeystorePath);

        try {
            var keyStore = getOrInitializeKeyStore();
            if (keyStore.containsAlias(alias)) {
                throw new IllegalStateException("Key already exists: " + alias);
            }

            char[] charKeyStorePassword = keyStorePassword.toCharArray();
            keyStore.setKeyEntry(
                    alias,
                    privateKey,
                    charKeyStorePassword,
                    new Certificate[]{certificate}
            );
            storeKeyStore(
                    pathToKeyStoreFile,
                    keyStore,
                    charKeyStorePassword
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not store signing key: " + alias, e);
        }
    }

    private synchronized KeyStore getOrInitializeKeyStore() {
        if (keyStore != null) {
            return keyStore;
        }

        try {
            var loadedKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            var pathToKeyStoreFile = Path.of(signingKeystorePath);
            char[] charKeyStorePassword = keyStorePassword.toCharArray();

            var keyStoreFileExists = Files.isRegularFile(Path.of(signingKeystorePath));

            if (keyStoreFileExists) {
                loadKeyStore(pathToKeyStoreFile, loadedKeyStore, charKeyStorePassword);
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
        return keyStore;
    }

    private void storeKeyStore(
            Path path,
            KeyStore keyStore,
            char[] charKeyStorePassword
    ) {
        if (Files.isDirectory(path)) {
            throw new RuntimeException("Must be a file");
        }

        try (OutputStream keyStoreOutputStream = Files.newOutputStream(path)) {
            keyStore.store(keyStoreOutputStream, charKeyStorePassword);
        } catch (CertificateException |
                 KeyStoreException |
                 NoSuchAlgorithmException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadKeyStore(
            Path path,
            KeyStore keyStore,
            char[] charKeyStorePassword
    ) {
        try (InputStream keyStoreData = Files.newInputStream(path)) {
            keyStore.load(keyStoreData, charKeyStorePassword);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }


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

}
