package io.github.janus.identity.application.port.out;

import io.github.janus.identity.domain.model.Credential;

import java.util.List;
import java.util.UUID;

public interface CredentialRepository {

    Credential findById(UUID id, UUID tenantId);
    void save(Credential credential);
    Credential update(Credential credential);
    void delete(UUID id,  UUID tenantId);
    List<Credential> findAll(UUID tenantId);

}
