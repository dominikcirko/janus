package io.github.janus.identity.role.adapter.out.persistence;

import io.github.janus.common.utils.RefUtils;
import io.github.janus.identity.role.application.port.out.RoleRepository;
import io.github.janus.identity.role.domain.model.Role;
import io.github.janus.tenant.adapter.out.persistence.TenantJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository repository;
    private final TenantJpaRepository tenantRepository;
    private final RolePersistenceMapper mapper;

    public RolePersistenceAdapter(RoleJpaRepository repository,
                                  TenantJpaRepository tenantRepository,
                                  RolePersistenceMapper mapper) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.mapper = mapper;
    }

    @Override
    public Role findById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return mapper.toDomain(entity);
    }

    @Override
    public Role findByName(String name) {
        var entity = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
        return mapper.toDomain(entity);
    }

    @Override
    public Role findByTenantId(UUID tenantId) {
        var entity = repository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Role not found with tenant id: " + tenantId));
        return mapper.toDomain(entity);
    }

    @Override
    public void save(Role role) {
        var entity = mapper.toEntity(role);
        var tenantRef = RefUtils.entityRef(tenantRepository, role.getTenantId());
        entity.setTenant(tenantRef);
        repository.save(entity);
    }

    @Override
    public Role update(Role role) {
        var entity = mapper.toEntity(role);
        var tenantRef = RefUtils.entityRef(tenantRepository, role.getTenantId());
        entity.setTenant(tenantRef);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
