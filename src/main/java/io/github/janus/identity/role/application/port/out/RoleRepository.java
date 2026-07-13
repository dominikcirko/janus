package io.github.janus.identity.role.application.port.out;

import io.github.janus.identity.role.domain.model.Role;

import java.util.UUID;

public interface RoleRepository {

    Role findById(UUID id);
    Role findByName(String name);
    Role findByTenantId(UUID tenantId);

    void save(Role role);
    Role update(Role role);
    void delete(UUID id);

}
