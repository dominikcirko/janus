package janus.common.security.token;

import janus.common.JwksKeyProvider;
import janus.common.security.token.exception.InvalidAccessTokenException;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class AccessTokenVerifier {

    /**
     * Enforces the OAuth scope-token syntax for each name after splitting the scope claim.
     * Requires one or more printable ASCII characters, excluding spaces, double quotes,
     * and backslashes, so malformed scope names are rejected before exposing granted scopes.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     */
    private static final Pattern OAUTH_SCOPE_TOKEN_PATTERN = Pattern.compile(
            "[\\x21\\x23-\\x5B\\x5D-\\x7E]+"
    );

    private final ObjectMapper objectMapper;
    private final long clockSkewSeconds;
    private final JwksKeyProvider jwksKeyProvider;
    private final String expectedAudience;
    private final TenantIssuerClient tenantIssuerClient;

    public VerifiedAccessToken verify(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidAccessTokenException("Access token must not be blank");
        }

        var decoder = Base64.getUrlDecoder();

        String[] parts = token.split("\\.", -1);
        if(parts.length != 3) throw new InvalidAccessTokenException("JWT must have 3 parts");
        for (String part : parts) {
            if (part.isEmpty()) {
                throw new InvalidAccessTokenException("JWT parts must not be empty");
            }
        }

        JsonNode headerNode;
        JsonNode payloadNode;
        byte[] signatureBytes;
        try {
            headerNode = objectMapper.readTree(decoder.decode(parts[0]));
            payloadNode = objectMapper.readTree(decoder.decode(parts[1]));
            signatureBytes = decoder.decode(parts[2]);
        } catch (IllegalArgumentException | JacksonException ex) {
            throw new InvalidAccessTokenException("Malformed JWT encoding or JSON", ex);
        }

        validateHeader(headerNode);
        validatePayload(payloadNode);

        String kid = requireText(headerNode, "kid");
        ECPublicKey trustedPublicKey = jwksKeyProvider.getKey(kid);

        validateSignature(parts[0], parts[1], signatureBytes, trustedPublicKey);
        Set<String> audiences = validateAudience(payloadNode);
        UUID tenantId = readTenantId(payloadNode);
        String issuer = requireText(payloadNode, "iss");
        if (!tenantIssuerClient.getIssuer(tenantId).equals(issuer)) {
            throw new InvalidAccessTokenException("Token issuer does not match its tenant");
        }

        return new VerifiedAccessToken(issuer, requireText(payloadNode, "sub"), tenantId,
                requireText(payloadNode, "client_id"), audiences, readScopes(payloadNode));
    }

    private void validateHeader(JsonNode headerNode) {
        requireObject(headerNode, "JWT header");

        String type = requireText(headerNode, "typ");
        if (!(type.equals("at+jwt")
                || type.equals("application/at+jwt"))) {
            throw new InvalidAccessTokenException("typ must be 'at+jwt' or 'application/at+jwt'");
        }

        String algorithm = requireText(headerNode, "alg");
        if (!algorithm.equals("ES256")) {
            throw new InvalidAccessTokenException("Signing algorithm must be 'ES256'");
        }
    }

    private void validatePayload(JsonNode payloadNode) {
        requireObject(payloadNode, "JWT payload");

        requireText(payloadNode, "iss");
        requireText(payloadNode, "sub");
        requireText(payloadNode, "client_id");
        requireText(payloadNode, "jti");

        JsonNode audience = requireClaim(payloadNode, "aud");

        if (audience.isString()) {
            requireText(payloadNode, "aud");
        } else if (audience.isArray() && !audience.isEmpty()) {
            for (JsonNode value : audience) {
                if (!value.isString() || value.stringValue().isBlank()) {
                    throw new InvalidAccessTokenException("aud must contain nonblank strings");
                }
            }
        } else {
            throw new InvalidAccessTokenException(
                    "aud must be a nonblank string or a nonempty array of nonblank strings"
            );
        }

        validateTimestamps(payloadNode);
    }

    private Set<String> validateAudience(JsonNode payloadNode) {
        JsonNode audience = requireClaim(payloadNode, "aud");
        var audiences = new HashSet<String>();

        if (audience.isString()) {
            audiences.add(audience.stringValue());
        } else {
            for (JsonNode value : audience) {
                audiences.add(value.stringValue());
            }
        }

        if (!audiences.contains(expectedAudience)) {
            throw new InvalidAccessTokenException("Token audience does not include this API");
        }
        return Set.copyOf(audiences);
    }

    private UUID readTenantId(JsonNode payloadNode) {
        String tenantId = requireText(payloadNode, "tenant_id");
        try {
            return UUID.fromString(tenantId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAccessTokenException("tenant_id must be a UUID", ex);
        }
    }

    private Set<String> readScopes(JsonNode payloadNode) {
        JsonNode scope = payloadNode.get("scope");
        if (scope == null) {
            return Set.of();
        }
        if (!scope.isString()) {
            throw new InvalidAccessTokenException("scope must be a space-separated string");
        }
        if (scope.stringValue().isEmpty()) {
            return Set.of();
        }

        var scopes = new HashSet<String>();
        for (String value : scope.stringValue().split(" ", -1)) {
            if (!OAUTH_SCOPE_TOKEN_PATTERN.matcher(value).matches()) {
                throw new InvalidAccessTokenException("scope must contain valid space-separated scope names");
            }
            scopes.add(value);
        }
        return Set.copyOf(scopes);
    }

    private void validateSignature(String encodedHeader, String encodedPayload,
            byte[] signatureBytes, ECPublicKey trustedPublicKey) {
        if (signatureBytes.length != 64) {
            throw new InvalidAccessTokenException("ES256 signature must be 64 bytes");
        }

        try {
            Signature verifier = Signature.getInstance("SHA256withECDSAinP1363Format");
            verifier.initVerify(trustedPublicKey);

            byte[] signingInput = (encodedHeader + "." + encodedPayload)
                    .getBytes(StandardCharsets.US_ASCII);

            verifier.update(signingInput);

            if (!verifier.verify(signatureBytes)) {
                throw new InvalidAccessTokenException("Invalid JWT signature");
            }
        } catch (GeneralSecurityException ex) {
            throw new InvalidAccessTokenException("JWT signature verification failed", ex);
        }
    }

    private void validateTimestamps(JsonNode payload) {
        JsonNode issuedAtNode = requireClaim(payload, "iat");
        JsonNode expiresAtNode = requireClaim(payload, "exp");
        JsonNode notBeforeNode = payload.get("nbf");

        if (!issuedAtNode.isIntegralNumber() || !issuedAtNode.canConvertToLong()) {
            throw new InvalidAccessTokenException(
                    "iat must be an integer timestamp within the supported range"
            );
        }

        if (!expiresAtNode.isIntegralNumber() || !expiresAtNode.canConvertToLong()) {
            throw new InvalidAccessTokenException("exp must be an integer timestamp within the supported range");
        }

        long issuedAt = issuedAtNode.longValue();
        long expiresAt = expiresAtNode.longValue();
        long now = Instant.now().getEpochSecond();

        if (expiresAt <= now - clockSkewSeconds) {
            throw new InvalidAccessTokenException("Access token has expired");
        }

        if (issuedAt > now + clockSkewSeconds) {
            throw new InvalidAccessTokenException("iat must not be in the future beyond the allowed clock skew");
        }

        if (expiresAt <= issuedAt) {
            throw new InvalidAccessTokenException("exp must be after iat");
        }

        if (notBeforeNode != null) {
            if (!notBeforeNode.isIntegralNumber()
                    || !notBeforeNode.canConvertToLong()) {
                throw new InvalidAccessTokenException("nbf must be an integer timestamp within the supported range");
            }

            if (notBeforeNode.longValue() > now + clockSkewSeconds) {
                throw new InvalidAccessTokenException("Access token is not yet valid");
            }
        }
    }

    private JsonNode requireClaim(JsonNode object, String name) {
        JsonNode value = object.get(name);

        if (value == null || value.isNull()) {
            throw new InvalidAccessTokenException(name + " must be present and not null");
        }

        return value;
    }

    private String requireText(JsonNode object, String name) {
        JsonNode value = requireClaim(object, name);

        if (!value.isString() || value.stringValue().isBlank()) {
            throw new InvalidAccessTokenException(name + " must be a nonblank string");
        }

        return value.stringValue();
    }

    private void requireObject(JsonNode value, String name) {
        if (value == null || !value.isObject()) {
            throw new InvalidAccessTokenException(name + " must be a JSON object");
        }
    }



}
