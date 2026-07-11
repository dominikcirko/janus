package io.github.janus.identity.permission.application.port.in.usecase;

import io.github.janus.identity.permission.domain.model.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionUseCase {
    Permission create(Permission permission);
    Permission update(UUID id, Permission permission);
    void delete(UUID id);
    Permission getById(UUID id);
    Permission getByName(String name);
    List<Permission> getAll();
}