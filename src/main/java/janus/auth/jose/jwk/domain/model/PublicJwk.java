package janus.auth.jose.jwk.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import janus.auth.jose.Algorithm;
import janus.auth.jose.jwk.domain.type.JwkKeyType;
import janus.auth.jose.jwk.domain.type.KeyOperation;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;

import java.util.List;
import java.util.Set;

//https://datatracker.ietf.org/doc/html/rfc7517#section-4
public sealed interface PublicJwk
        permits RSAPublicJwk, ECPublicJwk {

    @JsonProperty("kty") JwkKeyType kty();
    PublicKeyUse use();
    @JsonProperty("key_ops") Set<KeyOperation> keyOps();
    Algorithm alg();
    String kid();
    String x5u();
    List<String> x5c();
    String x5t();
    @JsonProperty("x5t#S256") String x5tS256();
}
