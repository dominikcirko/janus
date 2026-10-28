package janus.auth.oauth2.client.application.port.out;

import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClient;

import java.util.Optional;

public interface RegisteredClientRepository {
    RegisteredClient save(RegisteredClient client);
    Optional<RegisteredClient> findByClientId(String clientId);
}
