package janus.auth.jose.jwt.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

//https://datatracker.ietf.org/doc/html/rfc7519#section-4
public record ClaimsSet(String iss,
                        String sub,
                        String aud,
                        Instant exp,
                        Instant nbf,
                        Instant iat,
                        UUID jti,
                        Map<String, Object> customClaims) {}
