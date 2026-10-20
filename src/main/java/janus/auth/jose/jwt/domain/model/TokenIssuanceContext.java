package janus.auth.jose.jwt.domain.model;

import janus.auth.jose.jwt.domain.exception.InvalidTokenContextException;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

// separated from JWTClaimsSet with the purpose of separating contextual from server-generated fields in jwt
public record TokenIssuanceContext(
        String issuer,
        String subject,
        String audience,
        Duration tokenLifetime,
        Map<String, Object> customClaims) {

    private static final Set<String> REGISTERED_CLAIM_NAMES = Set.of(
            "iss", "sub", "aud", "exp", "nbf", "iat", "jti"
    );

    public TokenIssuanceContext {
        requireText(issuer, "issuer");
        requireText(subject, "subject");
        requireText(audience, "audience");

        if (tokenLifetime == null) {
            throw new InvalidTokenContextException("tokenLifetime must not be null");
        }

        if (tokenLifetime.toSeconds() <= 0) {
            throw new InvalidTokenContextException("tokenLifetime must be at least one second");
        }

        customClaims = copyCustomClaims(customClaims);
        rejectRegisteredClaims(customClaims);
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new InvalidTokenContextException(name + " must not be blank");
        }
    }

    private static Map<String, Object> copyCustomClaims(Map<String, Object> customClaims) {
        if (customClaims == null) {
            return Map.of();
        }
        return Map.copyOf(customClaims); // unmodifiable copy, NPE on null key/value
    }

    private static void rejectRegisteredClaims(Map<String, Object> customClaims) {
        for (String claimName : customClaims.keySet()) {
            if (REGISTERED_CLAIM_NAMES.contains(claimName)) {
                throw new InvalidTokenContextException("customClaims must not contain registered claim: " + claimName);
            }
        }
    }
}
