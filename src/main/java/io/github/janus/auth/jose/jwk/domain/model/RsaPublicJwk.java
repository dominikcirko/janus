package io.github.janus.auth.jose.jwk.domain.model;

import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.type.JwkKeyType;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;

public record RsaPublicJwk(
        PublicKeyUse use,
        List<KeyOperations> keyOps,
        Algorithm alg,
        String kid,
        List<String> x5c,
        String x5tS256,
        String n,
        String e
) implements JsonWebKey {

    @Override
    public JwkKeyType kty() {
        return JwkKeyType.RSA;
    }
}
