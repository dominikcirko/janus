package janus.auth.oauth2.client.adapter.out.persistence;

import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.adapter.out.persistence.mapper.RegisteredClientPersistenceMapper;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegisteredClientRepositoryAdapter implements RegisteredClientRepository {

    private final JpaRegisteredClientRepository repo;
    private final RegisteredClientPersistenceMapper mapper;

    @Override
    public RegisteredClient save(RegisteredClient client) {
        var entity = mapper.toEntity(client);
        var savedEntity = repo.save(entity);

        return mapper.toClient(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegisteredClient> findByClientId(String clientId) {
        return repo.findByClientId(clientId)
                .map(mapper::toClient);
    }
}
