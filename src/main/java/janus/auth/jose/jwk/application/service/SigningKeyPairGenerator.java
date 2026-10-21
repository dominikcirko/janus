package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.exception.SigningKeyGenerationException;
import janus.auth.jose.jwk.domain.type.Curve;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;

public final class SigningKeyPairGenerator {

    public static final int RSA_KEY_SIZE = 3072;
    public static final long RSA_PUBLIC_EXPONENT = 65537L;

    private SigningKeyPairGenerator() {
    }

    public static KeyPair generateRsaKeyPair() {
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance("RSA");
            var publicExponent = BigInteger.valueOf(RSA_PUBLIC_EXPONENT);
            var spec = new RSAKeyGenParameterSpec(RSA_KEY_SIZE, publicExponent);
            kpg.initialize(spec);
            return kpg.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new SigningKeyGenerationException("Could not generate RSA signing key", e);
        }
    }

    public static KeyPair generateEcKeyPair(Curve curve) {
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance("EC");
            var spec = specFor(curve);
            kpg.initialize(spec);
            return kpg.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new SigningKeyGenerationException("Could not generate EC signing key", e);
        }
    }

    private static ECGenParameterSpec specFor(Curve curve) {
        return switch (curve) {
            case P256 -> new ECGenParameterSpec("secp256r1");
            case P384 -> new ECGenParameterSpec("secp384r1");
            case P521 -> new ECGenParameterSpec("secp521r1");
        };
    }

}
