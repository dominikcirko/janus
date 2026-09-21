package janus.auth.jose.jws.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import janus.auth.jose.Algorithm;
import janus.auth.jose.JoseObjectType;
import janus.auth.jose.jwk.domain.model.PublicJwk;

import java.util.List;

//https://datatracker.ietf.org/doc/html/rfc7515#page-9
//https://datatracker.ietf.org/doc/html/rfc7519#section-5.1 - for typ in JWT
public record JwsHeader(
        Algorithm alg,
        String jku,
        PublicJwk jwk,
        String kid,
        String x5u,
        List<String> x5c,
        String x5t,
        @JsonProperty("x5t#S256") String x5tS256,
        JoseObjectType typ,
        String cty) {
}

