package io.github.janus.auth.jose.jwk.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

/**
 * Coordinates the signing keys required by Janus.
 *
 * <p>Provisioning is deliberately separate from JWK mapping: generation and
 * private-key storage happen here, while only public values are later exposed
 * through the JWKS workflow.</p>
 */
@Service
@RequiredArgsConstructor
public class SigningKeyManager {

    private static final String RSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final Pkcs12SigningKeyStore signingKeyStore;
    private final X509CertificateIssuer certificateIssuer;

    public synchronized void prepareSigningKeys(){
        prepareRsaSigningKey();
        // prepareRcSigningKey();
    }

    private void prepareRsaSigningKey() {
        String alias = Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS;
        if (signingKeyStore.containsAlias(alias)) {
            return;
        }

        try {
            KeyPair keyPair = SigningKeyPairGenerator.generateRsaKeyPair();
            X509Certificate certificate = certificateIssuer.issueSelfSigned(
                    keyPair,
                    RSA_CERTIFICATE_SIGNATURE_ALGORITHM
            );
            signingKeyStore.storePrivateKey(alias, keyPair.getPrivate(), certificate);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not provision RSA signing key", e);
        }
    }
}
