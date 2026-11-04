package janus.common.security.token;

import janus.common.JwksKeyProvider;
import janus.common.security.token.exception.InvalidAccessTokenException;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Base64;

@RequiredArgsConstructor
public final class AccessTokenVerifier {

    private final ObjectMapper objectMapper;
    private final long clockSkewSeconds;
    private final JwksKeyProvider jwksKeyProvider;
    private final String expectedAudience;

    public VerifiedAccessToken verify(String token) {

        var decoder = Base64.getUrlDecoder();

        String[] parts = token.split("\\.", -1);
        if(parts.length != 3) throw new InvalidAccessTokenException("JWT must have 3 parts");

        byte[] headerBytes = decoder.decode(parts[0]);
        byte[] payloadBytes = decoder.decode(parts[1]);
        byte[] signatureBytes = decoder.decode(parts[2]);

        JsonNode headerNode = objectMapper.readTree(headerBytes);
        validateHeader(headerNode);

        JsonNode payloadNode = objectMapper.readTree(payloadBytes);
        validatePayload(payloadNode);

        String kid = requireText(headerNode, "kid");
        ECPublicKey trustedPublicKey = jwksKeyProvider.getKey(kid);

        validateSignature(parts[0], parts[1], signatureBytes, trustedPublicKey);
        validateAudience(payloadNode);

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

    private void validateAudience(JsonNode payloadNode) {
        JsonNode audience = requireClaim(payloadNode, "aud");

        if (audience.isString()) {
            if (expectedAudience.equals(audience.stringValue())) {
                return;
            }
        } else if (audience.isArray()) {
            for (JsonNode value : audience) {
                if (value.isString() && expectedAudience.equals(value.stringValue())) {
                    return;
                }
            }
        }

        throw new InvalidAccessTokenException("Token audience does not include this API");
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
