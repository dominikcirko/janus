package io.github.janus.auth.jose.jwk.domain.model;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.type.Curve;
import io.github.janus.auth.jose.jwk.domain.type.JwkKeyType;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;

public record ECPublicJwk(
        PublicKeyUse use,
        List<KeyOperations> keyOps,
        Algorithm alg,
        String kid,
        String x5u,
        List<String> x5c,
        String x5t,
        String x5tS256,
        Curve crv, //https://datatracker.ietf.org/doc/html/rfc7518#section-6.2.1
        String x,
        String y
) implements PublicJwk {

    @Override
    public JwkKeyType kty() {
        return JwkKeyType.EC;
    }
}