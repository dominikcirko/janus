package io.github.janus.identity.credential.adapter.out.persistence;

import io.github.janus.identity.user.adapter.out.persistence.UserJpaRepository;
import io.github.janus.identity.credential.application.port.out.CredentialRepository;
import io.github.janus.identity.credential.domain.model.Credential;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CredentialPersistenceAdapter implements CredentialRepository {

    private final CredentialJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final CredentialPersistenceMapper mapper;

    public CredentialPersistenceAdapter(CredentialJpaRepository repository,
                                        UserJpaRepository userRepository,
                                        CredentialPersistenceMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Credential findById(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Cannot find credential with id: " + id));
    }

    @Override
    public Credential save(Credential credential) {
        var entity = mapper.toEntity(credential);
        entity.setUser(userRepository.getReferenceById(credential.getUserId()));

        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Credential update(Credential credential) {
        var entity = mapper.toEntity(credential);
        entity.setUser(userRepository.getReferenceById(credential.getUserId()));
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id, UUID userId) {
        repository.findByIdAndUserId(id, userId)
                .ifPresentOrElse(repository::delete,
                        () -> { throw new RuntimeException("Cannot find credential with id: " + id); });
    }

    @Override
    public List<Credential> findAllByUserId(UUID userId) {
        return mapper.toDomainList(repository.findAllByUserId(userId));
    }
}