package io.github.janus.identity.user.adapter.out.persistence;

import io.github.janus.common.utils.RefUtils;
import io.github.janus.identity.user.application.port.out.UserRepository;
import io.github.janus.identity.user.domain.model.User;
import io.github.janus.tenant.adapter.out.persistence.TenantJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final TenantJpaRepository tenantJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository repository,
                                  TenantJpaRepository tenantJpaRepository,
                                  UserPersistenceMapper mapper) {
        this.repository = repository;
        this.tenantJpaRepository = tenantJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Cannot find user with id: " + id));
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Cannot find user with email: " + email));
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Cannot find user with name: " + username));
    }

    @Override
    public User save(User user) {
        var saved = repository.save(mapper.toEntity(user));
        var tenantRef = RefUtils.entityRef(tenantJpaRepository, saved.getTenant().getId());
        saved.setTenant(tenantRef);
        return mapper.toDomain(saved);
    }

    @Override
    public User update(User user) {
        var saved = repository.save(mapper.toEntity(user));
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return mapper.toDomainList(repository.findAll());
    }
}
