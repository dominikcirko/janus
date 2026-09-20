package janus.auth.jose.jwk.application.service;

import janus.auth.jose.Algorithm;
import janus.auth.jose.jwk.common.JwkUtils;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.RSAPublicJwk;
import janus.auth.jose.jwk.domain.type.Curve;
import janus.auth.jose.jwk.domain.type.KeyOperations;
import janus.auth.jose.jwk.domain.type.PublicKeyUse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Component
public class PublicJwkMapper {

    public static final String SHA_256_THUMBPRINT = "SHA-256";
    public static final String SHA_1_THUMBPRINT = "SHA-1";

    @Value("${janus.x5u_base_url}")
    private String X5U_BASE_URL;

    public RSAPublicJwk toPublicRSA(
            X509Certificate certificate,
            PublicKeyUse use,
            List<KeyOperations> keyOps,
            Algorithm alg,
            String kid
    ) {
        if (!(certificate.getPublicKey() instanceof RSAPublicKey publicKey)) {
            throw new IllegalArgumentException("Certificate does not contain an RSA public key");
        }

        var x5u = X5U_BASE_URL + "/" + kid + ".pem";

        return new RSAPublicJwk(
                use,
                keyOps,
                alg,
                kid,
                x5u,
                List.of(JwkUtils.encodeCertificate(certificate)),
                JwkUtils.computeX5t(certificate, SHA_1_THUMBPRINT),
                JwkUtils.computeX5t(certificate, SHA_256_THUMBPRINT),
                JwkUtils.encodeBase64UrlUInt(publicKey.getModulus()),
                JwkUtils.encodeBase64UrlUInt(publicKey.getPublicExponent())
        );
    }

    public ECPublicJwk toPublicEC(
            X509Certificate certificate,
            PublicKeyUse use,
            List<KeyOperations> keyOps,
            Algorithm alg,
            String kid
    ) {
        if (!(certificate.getPublicKey() instanceof ECPublicKey publicKey)) {
            throw new IllegalArgumentException("Certificate does not contain an EC public key");
        }

        int fieldSize = publicKey.getParams()
                .getCurve()
                .getField()
                .getFieldSize();

        var crv = resolveCurve(fieldSize);

        var point = publicKey.getW();
        var x5u = X5U_BASE_URL + "/" + kid + ".pem";

        return new ECPublicJwk(
                use,
                keyOps,
                alg,
                kid,
                x5u,
                List.of(JwkUtils.encodeCertificate(certificate)),
                JwkUtils.computeX5t(certificate, SHA_1_THUMBPRINT),
                JwkUtils.computeX5t(certificate, SHA_256_THUMBPRINT),
                crv,
                JwkUtils.encodeEcCoordinate(point.getAffineX(), fieldSize),
                JwkUtils.encodeEcCoordinate(point.getAffineY(), fieldSize)
        );
    }

    private static Curve resolveCurve(int fieldSize) {
        return switch (fieldSize) {
            case 256 -> Curve.P256;
            case 384 -> Curve.P384;
            case 521 -> Curve.P521;
            default -> throw new IllegalArgumentException(
                    "Unsupported EC field size: " + fieldSize
            );
        };
    }

}
