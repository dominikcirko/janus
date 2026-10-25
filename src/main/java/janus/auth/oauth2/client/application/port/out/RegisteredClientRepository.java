package janus.auth.oauth2.client.application.port.out;

import janus.auth.oauth2.client.domain.model.RegisteredClientEntity;

import java.util.Optional;

public interface RegisteredClientRepository {
    RegisteredClientEntity save(RegisteredClientEntity client);
    Optional<RegisteredClientEntity> findByClientId(String clientId);
}
