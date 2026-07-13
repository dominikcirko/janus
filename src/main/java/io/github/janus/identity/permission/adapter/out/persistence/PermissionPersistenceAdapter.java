package io.github.janus.identity.permission.adapter.out.persistence;

import io.github.janus.identity.permission.application.port.out.PermissionRepository;
import io.github.janus.identity.permission.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionRepository {

    private final PermissionJpaRepository repository;
    private final PermissionPersistenceMapper mapper;

    @Override
    public Permission save(Permission permission) {
        var entity = mapper.toEntity(permission);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
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