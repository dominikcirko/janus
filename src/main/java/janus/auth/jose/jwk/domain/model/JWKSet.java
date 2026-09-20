package janus.auth.jose.jwk.domain.model;

import java.util.List;
import java.util.Map;

//https://datatracker.ietf.org/doc/html/rfc7517#section-5
public record JWKSet(List<PublicJwk> keys,
                     Map<String, Object> additionalProperties) {
}
