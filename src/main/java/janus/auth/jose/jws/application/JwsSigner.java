package janus.auth.jose.jws.application;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jws.domain.exception.JwsSigningException;
import janus.auth.jose.jws.domain.model.JwsHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwsSigner {

    private static final String SHA256_WITH_RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String SHA256_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA256withECDSAinP1363Format";
    private static final String SHA384_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA384withECDSAinP1363Format";
    private static final String SHA512_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA512withECDSAinP1363Format";

    private final ObjectMapper objectMapper;

    public String sign(JwsHeader header, byte[] payload, PrivateKey privateKey, PublicJwk jwk) {
        validateSigningInputs(header, jwk, payload, privateKey);
        validatePrivateKeyForAlgorithm(privateKey, header.alg());
        var headerBytes = objectMapper.writeValueAsBytes(header);
        var encoder = Base64.getUrlEncoder().withoutPadding();

        var encodedHeader = encoder.encodeToString(headerBytes);
        var encodedPayload = encoder.encodeToString(payload);

        var signingInput = encodedHeader + "." + encodedPayload;
        var signingInputBytes = signingInput.getBytes(StandardCharsets.US_ASCII);

        try {
            var signature = Signature.getInstance(getJcaSignatureAlgorithm(header.alg()));
            signature.initSign(privateKey);
            signature.update(signingInputBytes);

            var signatureBytes = signature.sign();
            var encodedSignature = encoder.encodeToString(signatureBytes);

            return signingInput + "." + encodedSignature;
        } catch (GeneralSecurityException e) {
            throw new JwsSigningException("Could not create JWS signature", e);
        }
    }

    private void validateSigningInputs(JwsHeader header, PublicJwk jwk, byte[] payload, PrivateKey privateKey){
        if (header == null || jwk == null || payload == null || privateKey == null
                || header.params() == null || header.params().kid() == null
                || header.alg() == null || jwk.kid() == null || jwk.alg() == null) {
            throw new JwsSigningException("Required JWS fields must not be null");
        }

        if (!header.params().kid().equals(jwk.kid())){
            throw new JwsSigningException("JWS Header's kid must be identical to public JWK's kid");
        }

        if (header.alg() != jwk.alg()) {
            throw new JwsSigningException("JWS Header's alg must be identical to public JWK's alg");
        }
    }

    /**
     * Ensures the private key type and EC curve match the requested JWS algorithm.
     */
    private void validatePrivateKeyForAlgorithm(PrivateKey privateKey, Algorithm alg) {
        switch (alg) {
            case RS256 -> {
                if (!(privateKey instanceof RSAPrivateKey)) {
                    throw new JwsSigningException("RS256 requires an RSA private key");
                }
            }

            case ES256, ES384, ES512 -> {
                if (!(privateKey instanceof ECPrivateKey ecPrivateKey)) {
                    throw new JwsSigningException(alg + " requires an EC private key");
                }

                int expectedFieldSize = switch (alg) {
                    case ES256 -> 256;
                    case ES384 -> 384;
                    case ES512 -> 521;
                    default -> throw new JwsSigningException("Unexpected algorithm: " + alg);
                };

                int actualFieldSize = ecPrivateKey.getParams()
                        .getCurve()
                        .getField()
                        .getFieldSize();

                if (actualFieldSize != expectedFieldSize) {
                    throw new JwsSigningException(alg + " requires EC field size " + expectedFieldSize);
                }
            }
        }
    }

    /**
     * Maps the RFC-defined JOSE algorithm to Java's equivalent Signature algorithm so the
     * signature scheme required by the protected header is used.
     */
    private String getJcaSignatureAlgorithm(Algorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> SHA256_WITH_RSA_SIGNATURE_ALGORITHM;
            case ES256 -> SHA256_WITH_ECDSA_SIGNATURE_ALGORITHM;
            case ES384 -> SHA384_WITH_ECDSA_SIGNATURE_ALGORITHM;
            case ES512 -> SHA512_WITH_ECDSA_SIGNATURE_ALGORITHM;
        };
    }

}
