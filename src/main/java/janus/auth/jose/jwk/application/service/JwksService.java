package janus.auth.jose.jwk.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.JWKSet;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import janus.auth.jose.jwk.domain.type.KeyOperation;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class JwksService {

    private final Pkcs12SigningKeyStore signingKeyStore;
    private final PublicJwkMapper publicJwkMapper;

    public JWKSet getPublicJwkSet() {
        var rsaPublicJwk = getRsaPublicJwk();
        var ecP256PublicJwk = getEcPublicJwk(Algorithm.ES256);
        var ecP384PublicJwk = getEcPublicJwk(Algorithm.ES384);
        var ecP521PublicJwk = getEcPublicJwk(Algorithm.ES512);

        return new JWKSet(
                List.of(rsaPublicJwk, ecP256PublicJwk, ecP384PublicJwk, ecP521PublicJwk),
                Map.of()
        );
    }

    public PublicJwk getActivePublicSigningJwk(Algorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> getRsaPublicJwk();
            case ES256, ES384, ES512 -> getEcPublicJwk(algorithm);
        };
    }

    private RSAPublicJwk getRsaPublicJwk(){
        var alias = Pkcs12SigningKeyStore.RSA_SIGNING_KEYSTORE_ALIAS;
        var certificate = signingKeyStore.getCertificate(alias);

        return publicJwkMapper.toPublicRSA(
                certificate,
                PublicKeyUse.SIG,
                Set.of(KeyOperation.VERIFY),
                Algorithm.RS256,
                alias
        );
    }

    private ECPublicJwk getEcPublicJwk(Algorithm algorithm) {
        var alias = switch (algorithm) {
            case ES256 -> Pkcs12SigningKeyStore.EC_P256_SIGNING_KEYSTORE_ALIAS;
            case ES384 -> Pkcs12SigningKeyStore.EC_P384_SIGNING_KEYSTORE_ALIAS;
            case ES512 -> Pkcs12SigningKeyStore.EC_P521_SIGNING_KEYSTORE_ALIAS;
            case RS256 -> throw new IllegalArgumentException("RS256 is not an EC signing algorithm");
        };

        var certificate = signingKeyStore.getCertificate(alias);

        return publicJwkMapper.toPublicEC(
                certificate,
                PublicKeyUse.SIG,
                Set.of(KeyOperation.VERIFY),
                algorithm,
                alias
        );
    }


}
