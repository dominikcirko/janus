package io.github.janus.auth.jose.jwk.common;

import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.util.Base64;

public class JwkEncodingUtils {

    // https://datatracker.ietf.org/doc/html/rfc7518#section-2
    public static String encodeBase64UrlUInt(BigInteger value) {
        byte[] unsignedBytes = BigIntegers.asUnsignedByteArray(value);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(unsignedBytes);
    }

}
