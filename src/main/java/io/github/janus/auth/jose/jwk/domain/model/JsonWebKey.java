package io.github.janus.auth.jose.jwk.domain.model;


import io.github.janus.auth.jose.Algorithm;
import io.github.janus.auth.jose.jwk.domain.type.JwkKeyType;
import io.github.janus.auth.jose.jwk.domain.type.KeyOperations;
import io.github.janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;

//https://datatracker.ietf.org/doc/html/rfc7517#section-4
public sealed interface JsonWebKey
        permits RSAPublicJwk, ECPublicJwk {

    JwkKeyType kty();
    PublicKeyUse use();
    List<KeyOperations> keyOps();
    Algorithm alg();
    String kid();
    List<String> x5c();
    String x5tS256();
}
