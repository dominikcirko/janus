package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.common.JwkEncodingUtils;
import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

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

        X509Certificate certificate =
                (X509Certificate) keyStore.getCertificate(RSA_SIGNING_KEYSTORE_ALIAS);

        RSAPublicKey publicKey =
                (RSAPublicKey) certificate.getPublicKey();

        return buildPublicRSA(
                publicKey,
                PublicKeyUse.SIG,
                List.of(KeyOperations.VERIFY),
                Algorithm.RS256,
                RSA_SIGNING_KEYSTORE_ALIAS);
    }

    private RSAPublicJwk buildPublicRSA(
            RSAPublicKey publicKey,
            PublicKeyUse use,
            List<KeyOperations> keyOps,
            Algorithm algorithm,
            String kid
    ){
        return new RSAPublicJwk(
                use,
                keyOps,
                algorithm,
                kid,
                List.of(), // todo later
                null, // todo later
                null, // todo later
                JwkEncodingUtils.encodeBase64UrlUInt(publicKey.getModulus()),
                JwkEncodingUtils.encodeBase64UrlUInt(publicKey.getPublicExponent())
        );
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
