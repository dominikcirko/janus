package io.github.janus.identity.application.port.out;

import io.github.janus.identity.domain.model.Credential;

import java.util.List;
import java.util.UUID;

public interface CredentialRepository {

    Credential findById(UUID id, UUID userId);
    Credential save(Credential credential);
    Credential update(Credential credential);
    void delete(UUID id, UUID userId);
    List<Credential> findAllByUserId(UUID userId);
}
