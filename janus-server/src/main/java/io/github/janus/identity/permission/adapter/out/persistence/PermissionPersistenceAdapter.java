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

    private final PermissionJpaRepository jpaRepository;
    private final PermissionPersistenceMapper mapper;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = mapper.toEntity(permission);
        PermissionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}