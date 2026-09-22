package janus.auth.jose.jws.application;

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

    private final ObjectMapper objectMapper;

    public String sign(JwsHeader header, byte[] payload, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        var headerBytes = objectMapper.writeValueAsBytes(header);
        var encoder = Base64.getUrlEncoder().withoutPadding();

        var encodedHeader = encoder.encodeToString(headerBytes);
        var encodedPayload = encoder.encodeToString(payload);

        var signingInput = encodedHeader + "." + encodedPayload;
        var signingInputBytes = signingInput.getBytes(StandardCharsets.US_ASCII);

        var signature = Signature.getInstance(header.alg().name());
        signature.initSign(privateKey);
        signature.update(signingInputBytes);

        var signatureBytes = signature.sign();
        var encodedSignature = encoder.encodeToString(signatureBytes);

        return signingInput + "." + encodedSignature;
    }

}
