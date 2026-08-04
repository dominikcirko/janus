package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

@RequiredArgsConstructor
public class PublicJwkMapper {

    @Value("${janus.signing.keystore.password}")
    private String keyStorePassword;

    @Value("${janus.signing.keystore.path}")
    private String signingKeystorePath;

    public RSAPublicJwk toPublicRSA() throws KeyStoreException {
        KeyPair rsaKeyPair;
        try {
            rsaKeyPair = SigningKeyPairGenerator.generateRsaKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e); //todo exception handling
        }

        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] charKeyStorePassword = keyStorePassword.toCharArray();

    }

    private void loadOrCreateKeyStore(char[] charKeyStorePassword, KeyStore ks){
        //todo exception handling
        File f = new File(signingKeystorePath);

        if(f.isFile()){
            loadKeyStore(f, ks, charKeyStorePassword);
        } else{
            initializeEmptyKeyStore(ks, charKeyStorePassword);
            ks.setKeyEntry();
            storeKeyStore(f, ks, charKeyStorePassword);
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
