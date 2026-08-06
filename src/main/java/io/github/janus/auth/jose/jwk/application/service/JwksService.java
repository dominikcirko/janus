package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.model.JWKSet;
import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * Builds the public JWK Set that Janus exposes to token verifiers.
 *
 * <p>It reads the certificate for the active signing key, maps its public key
 * to JWK fields, and returns it as a JWKS. The private key never enters this
 * workflow.</p>
 */
@Service
@RequiredArgsConstructor
public class JwksService {

    private final Pkcs12SigningKeyStore signingKeyStore;
    private final PublicJwkMapper publicJwkMapper;

    public JWKSet getPublicJwkSet() {
        String alias = Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS;
        X509Certificate certificate = signingKeyStore.getCertificate(alias);

        RSAPublicJwk rsaPublicJwk = publicJwkMapper.toPublicRSA(
                certificate,
                PublicKeyUse.SIG,
                List.of(KeyOperations.VERIFY),
                Algorithm.RS256,
                alias
        );

        return new JWKSet(List.of(rsaPublicJwk), Map.of());
    }
}
