package janus.auth.jose.jwk.domain.model;

import janus.auth.jose.Algorithm;
import janus.auth.jose.jwk.domain.type.JwkKeyType;
import janus.auth.jose.jwk.domain.type.KeyOperations;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;

public record RSAPublicJwk(
        PublicKeyUse use,
        List<KeyOperations> keyOps,
        Algorithm alg,
        String kid,
        String x5u,
        List<String> x5c,
        String x5t,
        String x5tS256,
        String n,
        String e
) implements PublicJwk {

    @Override
    public JwkKeyType kty() {
        return JwkKeyType.RSA;
    }
}
