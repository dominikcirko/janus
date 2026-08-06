package io.github.janus.auth.jose.jwk.common;

import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class JwkUtils {

    // https://datatracker.ietf.org/doc/html/rfc7518#section-2
    public static String encodeBase64UrlUInt(BigInteger value) {
        byte[] unsignedBytes = BigIntegers.asUnsignedByteArray(value);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(unsignedBytes);
    }

    public static String encodeCertificate(X509Certificate certificate) {
        try {
            return Base64.getEncoder()
                    .encodeToString(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new IllegalStateException(
                    "Could not encode certificate for JWK x5c", e
            );
        }
    }

    public static String computeX5tS256(X509Certificate certificate) {
        try {
            byte[] certificateDer = certificate.getEncoded();
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(certificateDer);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(digest);
        } catch (CertificateEncodingException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Could not calculate certificate SHA-256 thumbprint", e
            );
        }
    }

}
