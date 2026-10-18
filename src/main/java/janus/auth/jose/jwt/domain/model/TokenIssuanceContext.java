package janus.auth.jose.jwt.domain.model;

import janus.auth.jose.jwt.domain.exception.InvalidTokenContextException;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
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
        customClaims = customClaims == null ? Map.of() : Map.copyOf(customClaims); // copy to make it unmodifiable
        rejectRegisteredClaims(customClaims);

        Objects.requireNonNull(tokenLifetime);

        requireText(issuer, "issuer");
        requireText(subject, "subject");
        requireText(audience, "audience");

        if (tokenLifetime.toSeconds() <= 0) {
            throw new InvalidTokenContextException("tokenLifetime must be at least one second");
        }

    }

    private void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new InvalidTokenContextException(name + " must not be blank");
        }
    }

    // do not let user send registered claims through custom claims
    private static void rejectRegisteredClaims(Map<String, Object> customClaims) {
        for (String claimName : customClaims.keySet()) {
            if (REGISTERED_CLAIM_NAMES.contains(claimName)) {
                throw new InvalidTokenContextException("customClaims must not contain registered claim: " + claimName);
            }
        }
    }
}
