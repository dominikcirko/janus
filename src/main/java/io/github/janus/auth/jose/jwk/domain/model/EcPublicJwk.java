package io.github.janus.auth.jose.jwk.domain.model;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.type.Curve;
import io.github.janus.auth.jose.jwk.domain.type.JwkKeyType;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;

public record EcPublicJwk(
        PublicKeyUse use,
        List<KeyOperations> keyOps,
        Algorithm alg,
        String kid,
        List<String> x5c,
        String x5tS256,
        Curve crv,
        String x,
        String y
) implements JsonWebKey {

    @Override
    public JwkKeyType kty() {
        return JwkKeyType.EC;
    }
}