package io.github.janus.identity.permission.adapter.in.web;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;
import io.github.janus.identity.permission.application.port.in.usecase.PermissionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionUseCase useCase;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionRequest request) {
        var created = useCase.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest request) {
        var updated = useCase.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable UUID id) {
        var permission = useCase.getById(id);
        return ResponseEntity.ok(permission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
