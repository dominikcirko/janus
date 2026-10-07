package janus.auth.jose.jwt.domain.model;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

// separated from JWTClaimsSet with the purpose of separating contextual from server-generated fields in jwt
public record TokenIssuanceContext(
        String issuer,
        String subject,
        String audience,
        Duration tokenLifetime,
        Map<String, Object> customClaims) {

    public TokenIssuanceContext {
        customClaims = customClaims == null
                ? Map.of()
                : Map.copyOf(customClaims); // copy to make it unmodifiable
        Objects.requireNonNull(tokenLifetime);

        requireText(issuer, "issuer");
        requireText(subject, "subject");
        requireText(audience, "audience");

        if (tokenLifetime.toSeconds() <= 0) {
            throw new IllegalArgumentException(
                    "tokenLifetime must be at least one second"
            );
        }

    }

    public long tokenLifetimeSeconds() {
        return tokenLifetime.toSeconds();
    }

    private void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
