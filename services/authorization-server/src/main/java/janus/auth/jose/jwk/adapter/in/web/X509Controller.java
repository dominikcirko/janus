package janus.auth.jose.jwk.adapter.in.web;


import janus.auth.jose.jwk.application.service.Pkcs12SigningKeyStore;
import janus.auth.jose.jwk.common.PemUtils;
import janus.auth.jose.jwk.domain.exception.CertificateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certs")
@RequiredArgsConstructor
public class X509Controller {

    private final Pkcs12SigningKeyStore keyStore;

    public static final String APPLICATION_X509 = "application/pem-certificate-chain";

    @GetMapping(
            value = "/{kid}.pem",
            produces = APPLICATION_X509
    )
    public String getCertificate(@PathVariable String kid){
        var certificate = keyStore.findCertificate(kid)
                .orElseThrow(() -> new CertificateNotFoundException(kid));

        return PemUtils.encodeCertificate(certificate);
    }

}
