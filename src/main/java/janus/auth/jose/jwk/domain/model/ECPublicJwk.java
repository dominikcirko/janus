package janus.auth.jose.jwk.domain.model;

import janus.auth.jose.Algorithm;
import janus.auth.jose.jwk.domain.type.Curve;
import janus.auth.jose.jwk.domain.type.JwkKeyType;
import janus.auth.jose.jwk.domain.type.KeyOperation;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;
import java.util.Set;

public record ECPublicJwk(
        PublicKeyUse use,
        Set<KeyOperation> keyOps,
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