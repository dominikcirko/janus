package janus.auth.jose.jwk.domain.model;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.type.JwkKeyType;
import janus.auth.jose.jwk.domain.type.KeyOperation;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;
import java.util.Set;

public record RSAPublicJwk(
        PublicKeyUse use,
        Set<KeyOperation> keyOps,
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
