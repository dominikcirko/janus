package io.github.janus.identity.permission.application.port.out;

import io.github.janus.identity.permission.adapter.out.persistence.PermissionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    PermissionEntity save(PermissionEntity permission);
    Optional<PermissionEntity> findById(UUID id);
    Optional<PermissionEntity> findByName(String name);
    List<PermissionEntity> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
