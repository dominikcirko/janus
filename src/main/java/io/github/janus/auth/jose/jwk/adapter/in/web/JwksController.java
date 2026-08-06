package io.github.janus.auth.jose.jwk.adapter.in.web;


import io.github.janus.auth.jose.jwk.application.service.JwksService;
import io.github.janus.auth.jose.jwk.domain.model.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class JwksController {

    private final JwksService jwksService;
    private static final String APPLICATION_JWKS = "application/jwk-set+json";

    @GetMapping(
            value = "/jwks",
            produces = APPLICATION_JWKS
    )
    public JWKSet getJwks() {
        return jwksService.getPublicJwkSet();
    }

}
