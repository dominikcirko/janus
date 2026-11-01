package janus.identity.user.application.port.out;

import janus.identity.user.application.port.usecase.model.PasswordCredential;

import java.util.Optional;
import java.util.UUID;

public interface PasswordCredentialRepository {

    PasswordCredential save(PasswordCredential credential);
    Optional<PasswordCredential> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
