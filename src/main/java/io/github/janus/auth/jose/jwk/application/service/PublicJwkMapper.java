package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.common.JwkUtils;
import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

/**
 * Maps Java public-key representations to the public JSON Web Key model.
 *
 * <p>This class does not generate or store keys. It translates the public key
 * from a stored certificate into the values published by the JWKS endpoint.</p>
 */
@Component
public class PublicJwkMapper {

    @Value("${janus.x5u_base_url}")
    private String X5U_BASE_URL;

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

        var x5u = X5U_BASE_URL + "/" + kid + ".pem";

        return new RSAPublicJwk(
                use,
                keyOps,
                algorithm,
                kid,
                List.of(JwkUtils.encodeCertificate(certificate)),
                x5u,
                JwkUtils.computeX5tS256(certificate),
                JwkUtils.encodeBase64UrlUInt(publicKey.getModulus()),
                JwkUtils.encodeBase64UrlUInt(publicKey.getPublicExponent())
        );
    }

    //todo; toPublicEC

}
