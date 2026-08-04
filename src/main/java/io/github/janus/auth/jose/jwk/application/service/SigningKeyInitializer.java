package io.github.janus.auth.jose.jwk.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Runs signing-key provisioning once during application startup.
 *
 * <p>This makes the signing key available before Janus starts issuing tokens
 * or serving its public JWKS. Restarting the application reuses the key stored
 * under the existing alias.</p>
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "janus.signing.keystore",
        name = {"path", "password"}
)
public class SigningKeyInitializer implements ApplicationRunner {

    private final SigningKeyProvisioner signingKeyProvisioner;

    @Override
    public void run(ApplicationArguments args) {
        signingKeyProvisioner.ensureRsaSigningKey();
    }
}
