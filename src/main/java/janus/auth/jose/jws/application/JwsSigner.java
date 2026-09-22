package janus.auth.jose.jws.application;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jws.domain.model.JwsHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwsSigner {

    private static final String SHA256_WITH_RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String SHA256_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA256withECDSAinP1363Format";
    private static final String SHA384_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA384withECDSAinP1363Format";
    private static final String SHA512_WITH_ECDSA_SIGNATURE_ALGORITHM = "SHA512withECDSAinP1363Format";

    private final ObjectMapper objectMapper;

    public String sign(JwsHeader header, byte[] payload, PrivateKey privateKey, PublicJwk jwk)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        var headerBytes = objectMapper.writeValueAsBytes(header);
        var encoder = Base64.getUrlEncoder().withoutPadding();

        var encodedHeader = encoder.encodeToString(headerBytes);
        var encodedPayload = encoder.encodeToString(payload);

        var signingInput = encodedHeader + "." + encodedPayload;
        var signingInputBytes = signingInput.getBytes(StandardCharsets.US_ASCII);

        var signature = Signature.getInstance(getJcaSignatureAlgorithm(header.alg()));
        signature.initSign(privateKey);
        signature.update(signingInputBytes);

        validateSigningInputs(header, jwk);
        var signatureBytes = signature.sign();
        var encodedSignature = encoder.encodeToString(signatureBytes);

        return signingInput + "." + encodedSignature;
    }

    private void validateSigningInputs(JwsHeader header, PublicJwk jwk){
        if (header.alg() == null){
            throw new IllegalArgumentException("Algorithm must not be null");
        }

        if (!header.params().kid().equals(jwk.kid())){
            throw new IllegalStateException("JWS Header's kid must be identical to public JWK's kid");
        }
    }

    private String getJcaSignatureAlgorithm(Algorithm algorithm) {
        return switch (algorithm) {
            case RS256 -> SHA256_WITH_RSA_SIGNATURE_ALGORITHM;
            case ES256 -> SHA256_WITH_ECDSA_SIGNATURE_ALGORITHM;
            case ES384 -> SHA384_WITH_ECDSA_SIGNATURE_ALGORITHM;
            case ES512 -> SHA512_WITH_ECDSA_SIGNATURE_ALGORITHM;
        };
    }

}
