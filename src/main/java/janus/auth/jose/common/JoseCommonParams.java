package janus.auth.jose.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import janus.auth.jose.jwk.domain.model.PublicJwk;

import java.util.List;

public record JoseCommonParams(
        String jku,
        PublicJwk jwk,
        String kid,
        String x5u,
        List<String> x5c,
        String x5t,
        @JsonProperty("x5t#S256") String x5tS256,
        JoseObjectType typ,
        String cty) {}
