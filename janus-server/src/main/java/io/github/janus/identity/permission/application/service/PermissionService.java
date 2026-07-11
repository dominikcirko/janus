package io.github.janus.identity.permission.application.service;

import io.github.janus.identity.permission.application.port.in.usecase.PermissionUseCase;
import io.github.janus.identity.permission.application.port.out.PermissionRepository;
import io.github.janus.identity.permission.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCase {

    private final PermissionRepository repository;

    @Override
    public Permission create(Permission permission) {
        return repository.save(permission);
    }

    @Override
    public Permission update(UUID id, Permission permission) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        permission.setId(id);
        return repository.save(permission);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        repository.deleteById(id);
    }

    @Override
    public Permission getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public Permission getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public List<Permission> getAll() {
        return repository.findAll();
    }
}
