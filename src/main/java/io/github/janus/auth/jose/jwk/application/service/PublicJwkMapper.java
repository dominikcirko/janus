package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.common.JwkEncodingUtils;
import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;
import org.springframework.stereotype.Component;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;

/**
 * Maps Java public-key representations to the public JSON Web Key model.
 *
 * <p>This class does not generate or store keys. It translates the public key
 * from a stored certificate into the values published by the JWKS endpoint.</p>
 */
@Component
public class PublicJwkMapper {

    /**
     * Converts the RSA public key in a certificate to an RSA JWK. The caller
     * supplies JOSE metadata because intended use, operations, algorithm, and
     * key ID describe the workflow rather than the mathematical RSA key.
     */
    public RSAPublicJwk toPublicRSA(
            X509Certificate certificate,
            PublicKeyUse use,
            List<KeyOperations> keyOps,
            Algorithm algorithm,
            String kid
    ) {
        if (!(certificate.getPublicKey() instanceof RSAPublicKey publicKey)) {
            throw new IllegalArgumentException("Certificate does not contain an RSA public key");
        }

        return new RSAPublicJwk(
                use,
                keyOps,
                algorithm,
                kid,
                List.of(toX5cValue(certificate)), // todo later
                null, // todo later
                null, // todo later
                JwkEncodingUtils.encodeBase64UrlUInt(publicKey.getModulus()),
                JwkEncodingUtils.encodeBase64UrlUInt(publicKey.getPublicExponent())
        );
    }

    private String toX5cValue(X509Certificate certificate) {
        try {
            return Base64.getEncoder()
                    .encodeToString(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new IllegalStateException(
                    "Could not encode certificate for JWK x5c", e
            );
        }
    }

    //todo; toPublicEC

}
