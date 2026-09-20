package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.type.Curve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class SigningKeyManager {

    private static final String RSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String SHA256_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String SHA384_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA384withECDSA";
    private static final String SHA512_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM = "SHA512withECDSA";

    private final Pkcs12SigningKeyStore signingKeyStore;
    private final X509CertificateIssuer certificateIssuer;

    public void prepareSigningKeys(){
        prepareRsaSigningKey();
        prepareEcSigningKey(Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS, Curve.P256);
        prepareEcSigningKey(Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS, Curve.P384);
        prepareEcSigningKey(Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS, Curve.P521);
    }

    private void prepareRsaSigningKey() {
        String alias = Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS;
        if (signingKeyStore.containsAlias(alias)) {
            return;
        }
        try {
            var keyPair = SigningKeyPairGenerator.generateRsaKeyPair();
            var certificate = certificateIssuer.issueSelfSigned(
                    keyPair,
                    RSA_CERTIFICATE_SIGNATURE_ALGORITHM
            );
            signingKeyStore.storePrivateKey(alias, keyPair.getPrivate(), certificate);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not provision RSA signing key", e);
        }
    }

    private void prepareEcSigningKey(String alias, Curve crv){
        if (signingKeyStore.containsAlias(alias)) {
            return;
        }
        var signatureAlgorithm = getSignatureAlgorithmFromCrv(crv);
        try {
            var keyPair = SigningKeyPairGenerator.generateEcKeyPair(crv);
            var certificate = certificateIssuer.issueSelfSigned(
                    keyPair,
                    signatureAlgorithm
            );
            signingKeyStore.storePrivateKey(alias, keyPair.getPrivate(), certificate);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not provision EC signing key", e);
        }
    }

    private String getSignatureAlgorithmFromCrv(Curve crv){
        return switch (crv) {
            case P256 -> SHA256_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM;
            case P384 -> SHA384_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM;
            case P521 -> SHA512_WITH_ECDSA_CERTIFICATE_SIGNATURE_ALGORITHM;
        };
    }


}
