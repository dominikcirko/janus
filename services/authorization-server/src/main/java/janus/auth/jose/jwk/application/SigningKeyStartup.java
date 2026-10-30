package janus.auth.jose.jwk.application;

import janus.auth.jose.jwk.application.service.SigningKeyManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Checks Janus's signing keys once during application startup.
 *
 * <p>This makes the signing key available before Janus starts issuing tokens
 * or serving its public JWKS. Restarting the application reuses the key stored
 * under the existing alias.</p>
 */
@Component
@RequiredArgsConstructor
public class SigningKeyStartup implements ApplicationRunner {

    private final SigningKeyManager signingKeyManager;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        signingKeyManager.prepareSigningKeys();
    }
}
