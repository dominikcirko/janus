package io.github.janus.identity.permission.adapter.out.persistence;

import io.github.janus.identity.permission.application.port.out.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionRepository {

    private final PermissionJpaRepository repository;

    @Override
    public PermissionEntity save(PermissionEntity permission) {
        return repository.save(permission);
    }

    @Override
    public Optional<PermissionEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<PermissionEntity> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<PermissionEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}