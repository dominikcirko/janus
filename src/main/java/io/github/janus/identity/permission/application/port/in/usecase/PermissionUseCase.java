package io.github.janus.identity.permission.application.port.in.usecase;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;

import java.util.List;
import java.util.UUID;

public interface PermissionUseCase {
    PermissionResponse create(PermissionRequest request);
    PermissionResponse update(UUID id, PermissionRequest request);
    void delete(UUID id);
    PermissionResponse getById(UUID id);
    PermissionResponse getByName(String name);
    List<PermissionResponse> getAll();
}