package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.type.Curve;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

public final class SigningKeyPairGenerator {

    public static final int RSA_KEY_SIZE = 3072;
    public static final long RSA_PUBLIC_EXPONENT = 65537L;

    private SigningKeyPairGenerator() {
    }

    public static KeyPair generateRsaKeyPair()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        var kpg = KeyPairGenerator.getInstance("RSA");
        var publicExponent = BigInteger.valueOf(RSA_PUBLIC_EXPONENT);
        var spec = new RSAKeyGenParameterSpec(RSA_KEY_SIZE, publicExponent);
        kpg.initialize(spec);

        return kpg.generateKeyPair();
    }

    public static KeyPair generateEcKeyPair(Curve crv)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        var kpg = KeyPairGenerator.getInstance("EC");
        var spec = specFor(crv);
        kpg.initialize(spec);

        return kpg.generateKeyPair();
    }

    private static ECGenParameterSpec specFor(Curve curve) {
        return switch (curve) {
            case P256 -> new ECGenParameterSpec("secp256r1");
            case P384 -> new ECGenParameterSpec("secp384r1");
            case P521 -> new ECGenParameterSpec("secp521r1");
        };
    }

}
