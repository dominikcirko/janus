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


    }

    private void loadOrCreateKeyStore(
            char[] charKeyStorePassword,
            KeyStore keyStore,
            KeyPair keyPair,
            String keyStoreAlias,
            String signatureAlgorithm) throws KeyStoreException, NoSuchAlgorithmException {
        //todo exception handling
        File file = new File(signingKeystorePath);

        if(file.isFile()){
            loadKeyStore(file, keyStore, charKeyStorePassword);
        } else{
            initializeEmptyKeyStore(keyStore, charKeyStorePassword);
            X509CertificateIssuer x509issuer = new X509CertificateIssuer();
            X509Certificate certificate = x509issuer.issueSelfSigned(keyPair, signatureAlgorithm);

            Certificate[] certificateChain = {certificate};

            keyStore.setKeyEntry(
                    keyStoreAlias,
                    keyPair.getPrivate(),
                    charKeyStorePassword,
                    certificateChain
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
