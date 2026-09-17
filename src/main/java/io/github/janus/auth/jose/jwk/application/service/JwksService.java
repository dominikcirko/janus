package io.github.janus.auth.jose.jwk.application.service;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.model.ECPublicJwk;
import io.github.janus.auth.jose.jwk.domain.model.JWKSet;
import io.github.janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        var rsaPublicJwk = getRsaPublicJwk();
        var ecP256PublicJwk = getEcPublicJwk(Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS, Algorithm.ES256);
        var ecP384PublicJwk = getEcPublicJwk(Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS, Algorithm.ES384);
        var ecP521PublicJwk = getEcPublicJwk(Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS, Algorithm.ES512);

        return new JWKSet(
                List.of(rsaPublicJwk, ecP256PublicJwk, ecP384PublicJwk, ecP521PublicJwk),
                Map.of()
        );
    }

    private RSAPublicJwk getRsaPublicJwk(){
        var alias = Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS;
        var certificate = signingKeyStore.getCertificate(alias);

        return publicJwkMapper.toPublicRSA(
                certificate,
                PublicKeyUse.SIG,
                List.of(KeyOperations.VERIFY),
                Algorithm.RS256,
                alias
        );
    }

    private ECPublicJwk getEcPublicJwk(String alias, Algorithm algorithm){
        var certificate = signingKeyStore.getCertificate(alias);

        return publicJwkMapper.toPublicEC(
                certificate,
                PublicKeyUse.SIG,
                List.of(KeyOperations.VERIFY),
                algorithm,
                alias
        );
    }
}
