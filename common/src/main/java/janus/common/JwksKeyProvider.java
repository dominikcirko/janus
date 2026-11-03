package janus.common;

import janus.common.security.token.exception.InvalidAccessTokenException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.math.BigInteger;
import java.net.URI;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public final class JwksKeyProvider {

    private static final Duration CACHE_LIFETIME = Duration.ofMinutes(5);
    private static final Duration REFRESH_INTERVAL = Duration.ofSeconds(30);

    private final RestClient restClient;
    private final URI jwksUri;

    private Map<String, ECPublicKey> cachedKeys = Map.of();
    private Instant expiresAt = Instant.MIN;
    private Instant nextRefreshAt = Instant.MIN;
    private RuntimeException refreshFailure;

    public JwksKeyProvider(RestClient restClient, URI jwksUri) {
        this.restClient = restClient;
        this.jwksUri = jwksUri;
    }

    public synchronized ECPublicKey getKey(String kid) {
        if (kid == null || kid.isBlank()) {
            throw new InvalidAccessTokenException("kid must be present");
        }

        Instant now = Instant.now();
        ECPublicKey cachedKey = cachedKeys.get(kid);

        if (cachedKey != null && now.isBefore(expiresAt)) {
            return cachedKey;
        }

        if (!now.isBefore(nextRefreshAt)) {
            nextRefreshAt = now.plus(REFRESH_INTERVAL);

            try {
                cachedKeys = fetchKeys();
                expiresAt = Instant.now().plus(CACHE_LIFETIME);
                refreshFailure = null;
            } catch (RuntimeException ex) {
                refreshFailure = ex;
                throw ex;
            }
        } else {
            if (refreshFailure != null) {
                throw refreshFailure;
            }

            if (!now.isBefore(expiresAt)) {
                throw new IllegalStateException("Signing-key cache expired; refresh temporarily throttled");
            }
        }

        ECPublicKey key = cachedKeys.get(kid);

        if (key == null) {
            throw new InvalidAccessTokenException("Unknown signing key");
        }

        return key;
    }

    private Map<String, ECPublicKey> fetchKeys() {
        JsonNode jwks = restClient.get()
                .uri(jwksUri)
                .retrieve()
                .body(JsonNode.class);

        if (jwks == null) {
            throw new IllegalStateException("Empty JWKS response");
        }

        JsonNode keys = jwks.get("keys");

        if (keys == null || !keys.isArray()) {
            throw new IllegalStateException("JWKS response must contain a keys array");
        }

        var result = new HashMap<String, ECPublicKey>();

        for (JsonNode jwk : keys) {
            if (!isEs256VerificationKey(jwk)) {
                continue;
            }

            String kid = text(jwk, "kid");

            if (kid == null || kid.isBlank()) {
                throw new IllegalStateException("Signing JWK is missing kid");
            }

            if (result.putIfAbsent(kid, toPublicKey(jwk)) != null) {
                throw new IllegalStateException("Duplicate signing-key kid");
            }
        }

        return Map.copyOf(result);
    }

    private boolean isEs256VerificationKey(JsonNode jwk) {
        if (!"EC".equals(text(jwk, "kty")) ||
                !"P-256".equals(text(jwk, "crv"))) return false;

        if (jwk.has("alg") &&
                !"ES256".equals(text(jwk, "alg"))) return false;

        if (jwk.has("use") &&
                !"sig".equals(text(jwk, "use"))) return false;


        if (jwk.has("key_ops")) {
            JsonNode operations = jwk.get("key_ops");

            if (!operations.isArray()) throw new IllegalStateException("Invalid JWK key_ops");

            boolean permitsVerification = false;

            for (JsonNode operation : operations) {
                if (!operation.isString()) {
                    throw new IllegalStateException("JWK key_ops must contain strings");
                }
                if ("verify".equals(operation.stringValue())) {
                    permitsVerification = true;
                }
            }
            return permitsVerification;
        }
        return true;
    }

    private ECPublicKey toPublicKey(JsonNode jwk) {
        try {
            String x = text(jwk, "x");
            String y = text(jwk, "y");

            if (x == null || y == null) {
                throw new IllegalStateException("EC JWK requires x and y");
            }

            var decoder = Base64.getUrlDecoder();
            byte[] xBytes = decoder.decode(x);
            byte[] yBytes = decoder.decode(y);

            if (xBytes.length != 32 || yBytes.length != 32) {
                throw new IllegalStateException(
                        "P-256 coordinates must each be 32 bytes"
                );
            }

            var parameters = AlgorithmParameters.getInstance("EC");
            parameters.init(new ECGenParameterSpec("secp256r1"));

            var point = new ECPoint(
                    new BigInteger(1, xBytes),
                    new BigInteger(1, yBytes)
            );

            var keySpec = new ECPublicKeySpec(
                    point,
                    parameters.getParameterSpec(ECParameterSpec.class)
            );

            return (ECPublicKey) KeyFactory.getInstance("EC")
                    .generatePublic(keySpec);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid EC signing key", ex);
        }
    }

    private String text(JsonNode object, String name) {
        JsonNode value = object.get(name);

        if (value == null || !value.isString()) {
            return null;
        }

        return value.stringValue();
    }
}
