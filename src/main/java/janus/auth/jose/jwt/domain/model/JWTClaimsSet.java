package janus.auth.jose.jwt.domain.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

//https://datatracker.ietf.org/doc/html/rfc7519#section-4
// not using record because we want to restrict object creation to be only through context object,
// which is not possible with records because of their canonical constructor that can not be private
@Getter
public final class JWTClaimsSet {

    private final String iss;
    private final String sub;
    private final String aud;
    private final long exp;
    private final long nbf;
    private final long iat;
    private final String jti;
    @Getter(AccessLevel.NONE)
    private final Map<String, Object> customClaims;

    public JWTClaimsSet(TokenIssuanceContext context) {
        long now = Instant.now().getEpochSecond();

        this.iss = context.issuer();
        this.sub = context.subject();
        this.aud = context.audience();
        this.iat = now;
        this.exp = Math.addExact(this.iat, context.tokenLifetimeSeconds());
        this.nbf = now;
        this.jti = UUID.randomUUID().toString();
        this.customClaims = context.customClaims();
    }
    // custom claims must be serialized at the same top level as registered jwt claims
    @JsonAnyGetter
    public Map<String, Object> getCustomClaims() {
        return customClaims;
    }
}
