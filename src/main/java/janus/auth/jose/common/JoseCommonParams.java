package janus.auth.jose.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import janus.auth.jose.jwk.domain.model.PublicJwk;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
