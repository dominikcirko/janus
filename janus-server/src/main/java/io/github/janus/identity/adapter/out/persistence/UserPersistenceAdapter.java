package io.github.janus.identity.adapter.out.persistence;

import io.github.janus.identity.adapter.out.persistence.mapper.UserPersistenceMapper;
import io.github.janus.identity.adapter.out.persistence.repository.UserJpaRepository;
import io.github.janus.identity.application.port.out.UserRepository;
import io.github.janus.identity.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User findById(UUID userId,  UUID tenantId) {
        var user = repository.findById(userId, tenantId);
        return mapper.toDomain(Optional.of(user));
    }

    @Override
    public User findByEmail(String email, UUID tenantId) {
        var user = repository.findByEmail(email, tenantId);
        return mapper.toDomain(Optional.of(user));
    }

    @Override
    public User findByUsername(String username, UUID tenantId) {
        var user = repository.findByUsername(username, tenantId);
        return mapper.toDomain(Optional.of(user));
    }

    @Override
    public void save(User user) {
        var entity = mapper.toEntity(user);
        var saved = repository.save(entity);
        mapper.toDomain(Optional.of(saved));
    }

    @Override
    public User update(User user) {
        var entity = mapper.toEntity(user);
        var saved = repository.save(entity);
        return mapper.toDomain(Optional.of(saved));
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        repository.deleteById(id, tenantId);
    }

    @Override
    public List<User> findAll(UUID tenantId) {
        var entities = repository.findAllByTenantId(tenantId);
        return mapper.toDomainList(entities);
    }
}
