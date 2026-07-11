package io.github.janus.identity.role.adapter.out.persistence;

import io.github.janus.identity.role.application.port.out.RoleRepository;
import io.github.janus.identity.role.domain.model.Role;

import java.util.UUID;

public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository repository;
    private final RolePersistenceMapper mapper;

    public RolePersistenceAdapter(RoleJpaRepository repository,
                                  RolePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Role findById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Override
    public Role findByName(String name) {
        return null;
    }

    @Override
    public Role findByTenant(UUID tenantId) {
        return null;
    }

    @Override
    public void save(Role role) {

    }

    @Override
    public Role update(Role role) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
