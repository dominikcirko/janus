package janus.auth.jose.jwk.common;

import janus.auth.jose.jwk.domain.exception.PemEncodingException;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class PemUtils {

    public static final int BASE64_LINE_LENGTH = 64;

    public static String encodeCertificate(X509Certificate certificate) {
        try {
            byte[] derCertificate = certificate.getEncoded();

            Base64.Encoder pemEncoder = Base64.getMimeEncoder(
                    BASE64_LINE_LENGTH,
                    "\n".getBytes(StandardCharsets.US_ASCII)
            );

            String base64Certificate = pemEncoder.encodeToString(derCertificate);

            return "-----BEGIN CERTIFICATE-----\n"
                    + base64Certificate
                    + "\n-----END CERTIFICATE-----\n";
        } catch (CertificateEncodingException e) {
            throw new PemEncodingException("Could not encode certificate as PEM", e);
        }
    }

}
