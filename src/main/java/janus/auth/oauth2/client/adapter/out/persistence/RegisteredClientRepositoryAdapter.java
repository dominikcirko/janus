package janus.auth.oauth2.client.adapter.out.persistence;

import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegisteredClientRepositoryAdapter implements RegisteredClientRepository {

    private final JpaRegisteredClientRepository repo;

    @Override
    public RegisteredClientEntity save(RegisteredClientEntity client) {
        return repo.save(client);
    }

    @Override
    public Optional<RegisteredClientEntity> findByClientId(String clientId) {
        return repo.findByClientId(clientId);
    }
}
