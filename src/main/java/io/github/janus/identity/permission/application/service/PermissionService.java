package io.github.janus.identity.permission.application.service;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;
import io.github.janus.identity.permission.application.port.in.mapper.PermissionApplicationMapper;
import io.github.janus.identity.permission.application.port.in.usecase.PermissionUseCase;
import io.github.janus.identity.permission.application.port.out.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCase {

    private final PermissionApplicationMapper mapper;
    private final PermissionRepository repository;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        var entity = mapper.toEntity(request);
        var savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    public PermissionResponse update(UUID id, PermissionRequest request) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        var entity = mapper.toEntity(request);
        entity.setId(id);
        var savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        repository.deleteById(id);
    }

    @Override
    public PermissionResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public PermissionResponse getByName(String name) {
        return repository.findByName(name)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public List<PermissionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
