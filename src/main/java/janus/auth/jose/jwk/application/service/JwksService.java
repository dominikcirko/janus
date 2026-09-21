package janus.auth.jose.jwk.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.JWKSet;
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
                Set.of(KeyOperation.VERIFY),
                Algorithm.RS256,
                alias
        );
    }

    private ECPublicJwk getEcPublicJwk(String alias, Algorithm algorithm){
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
