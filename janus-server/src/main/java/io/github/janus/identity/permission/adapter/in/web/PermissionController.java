package io.github.janus.identity.permission.adapter.in.web;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;
import io.github.janus.identity.permission.adapter.in.web.mapper.PermissionWebMapper;
import io.github.janus.identity.permission.application.port.in.usecase.PermissionUseCase;
import io.github.janus.identity.permission.domain.model.Permission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionUseCase useCase;
    private final PermissionWebMapper mapper;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionRequest request) {
        Permission created = useCase.create(mapper.toDomain(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest request) {
        Permission updated = useCase.update(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable UUID id) {
        Permission permission = useCase.getById(id);
        return ResponseEntity.ok(mapper.toResponse(permission));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> search(
            @RequestParam(required = false) String name) {
        if (name != null) {
            Permission permission = useCase.getByName(name);
            return ResponseEntity.ok(List.of(mapper.toResponse(permission)));
        }
        List<Permission> permissions = useCase.getAll();
        return ResponseEntity.ok(mapper.toResponseList(permissions));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
