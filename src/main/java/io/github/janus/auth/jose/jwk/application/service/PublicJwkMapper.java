package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RequiredArgsConstructor
public class PublicJwkMapper {

    @Value("${janus.signing.keystore.password}")
    private String keyStorePassword;

    @Value("${janus.signing.keystore.path}")
    private String signingKeystorePath;

    public static String RSA_SIGNING_KEYSTORE_ALIAS = "janus-signing-rsa-1";
    public static String EC_P256_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p256-1";
    public static String EC_P384_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p384-1";
    public static String EC_P521_SIGNING_KEYSTORE_ALIAS = "janus-signing-ec-p521-1";

    public RSAPublicJwk toPublicRSA() throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] charKeyStorePassword = keyStorePassword.toCharArray();
        KeyPair rsaKeyPair = SigningKeyPairGenerator.generateRsaKeyPair();

        loadOrCreateKeyStore(
                charKeyStorePassword,
                keyStore,
                rsaKeyPair,
                RSA_SIGNING_KEYSTORE_ALIAS,
                "SHA256withRSA");

        // todo: map to public & return
    }

    private void loadOrCreateKeyStore(
            char[] charKeyStorePassword,
            KeyStore keyStore,
            KeyPair keyPair,
            String keyStoreAlias,
            String signatureAlgorithm) throws KeyStoreException {
        //todo exception handling
        File file = new File(signingKeystorePath);

        if (file.isFile()) {
            loadKeyStore(file, keyStore, charKeyStorePassword);
        } else {
            initializeEmptyKeyStore(keyStore, charKeyStorePassword);
        }

        if (!keyStore.containsAlias(keyStoreAlias)) {
            X509Certificate certificate =
                    new X509CertificateIssuer().issueSelfSigned(keyPair, signatureAlgorithm);

            keyStore.setKeyEntry(
                    keyStoreAlias,
                    keyPair.getPrivate(),
                    charKeyStorePassword,
                    new Certificate[]{certificate}
            );

            storeKeyStore(file, keyStore, charKeyStorePassword);
        }
    }

    private void loadKeyStore(File f, KeyStore ks, char[] charKeyStorePassword){
        //todo exception handling
        try(InputStream keyStoreData = new FileInputStream(f)){
            ks.load(keyStoreData, charKeyStorePassword);
        } catch (CertificateException |
                 NoSuchAlgorithmException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeEmptyKeyStore(KeyStore ks, char[] charKeyStorePassword){
        //todo exception handling
        try {
            ks.load(null, charKeyStorePassword);
        } catch (IOException |
                 NoSuchAlgorithmException |
                 CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeKeyStore(File f, KeyStore ks, char[] charKeyStorePassword){
        //todo exception handling
        if(f.isDirectory()){
            throw new RuntimeException("Must be a file");
        }
        try (FileOutputStream keyStoreOutputStream = new FileOutputStream(f)) {
            ks.store(keyStoreOutputStream, charKeyStorePassword);
        } catch (IOException |
                 CertificateException |
                 KeyStoreException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }







}
