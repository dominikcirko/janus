package janus.auth.jose.jwk.application.service;

import janus.auth.jose.jwk.domain.exception.CertificateIssuanceException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class X509CertificateIssuer {

    public X509Certificate issueSelfSigned(KeyPair keyPair, String signatureAlgorithm) {
        var issuer = new X500Name("CN=Janus Self-Signed Certificate, C=HR");
        var subject = issuer; //self-signed -> issuer == subject

        //https://datatracker.ietf.org/doc/html/rfc5280#section-4.1.2.2
        //serial can be max 20 octets / 160 bits
        var serial = new BigInteger(128, new SecureRandom()).setBit(127);

        var now = Instant.now();

        var notBefore = Date.from(now.minus(Duration.ofMinutes(5)));
        var notAfter  = Date.from(now.plus(Duration.ofDays(3650)));

        var publicKeyInfo = SubjectPublicKeyInfo
                .getInstance(keyPair.getPublic().getEncoded());

        var certBuilder = new X509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                subject,
                publicKeyInfo
        );

        try {
            addExtensions(certBuilder, keyPair);
            ContentSigner sigGen = new JcaContentSignerBuilder(signatureAlgorithm)
                    .build(keyPair.getPrivate());
            X509CertificateHolder holder = certBuilder.build(sigGen);

            return new JcaX509CertificateConverter()
                    .getCertificate(holder);
        } catch (NoSuchAlgorithmException | CertIOException | OperatorCreationException | CertificateException e) {
            throw new CertificateIssuanceException("Could not issue certificate", e);
        }

    }

    private void addExtensions(X509v3CertificateBuilder certBuilder, KeyPair keyPair)
            throws NoSuchAlgorithmException, CertIOException {

            var extensionUtils = new JcaX509ExtensionUtils();

            // this is not a CA certificate, it cannot be used to issue other certifications
            certBuilder.addExtension(
                    Extension.basicConstraints,
                    true,
                    new BasicConstraints(false)
            );
            certBuilder.addExtension(
                    Extension.keyUsage,
                    true,
                    new KeyUsage(KeyUsage.digitalSignature)
            );
            certBuilder.addExtension(
                    Extension.subjectKeyIdentifier,
                    false,
                    extensionUtils.createSubjectKeyIdentifier(keyPair.getPublic())
            );
            // ID of the key that signed this certificate, same because it is self-signed
            certBuilder.addExtension(
                    Extension.authorityKeyIdentifier,
                    false,
                    extensionUtils.createAuthorityKeyIdentifier(keyPair.getPublic())
            );
    }

}
