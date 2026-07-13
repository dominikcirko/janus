package io.github.janus.identity.permission.application.port.out;

import io.github.janus.identity.permission.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    Permission save(Permission permission);
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
