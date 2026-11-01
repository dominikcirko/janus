package janus.tenancy.tenant.adapter.in.web;

import jakarta.validation.Valid;
import janus.tenancy.tenant.adapter.in.web.dto.TenantRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantStatusRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantUpdateRequest;
import janus.tenancy.tenant.adapter.in.web.mapper.TenantWebMapper;
import janus.tenancy.tenant.application.port.usecase.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}")
@RequiredArgsConstructor
public class TenantController {

    private final TenantUseCase tenantUseCase;
    private final TenantWebMapper tenantWebMapper;

    @PostMapping(
            value = "/organizations/{organizationId}/tenants",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TenantResponse> createTenant(
            @PathVariable UUID organizationId, @Valid @RequestBody TenantRequest request) {
        var input = tenantWebMapper.toInput(request);
        var output = tenantUseCase.createTenant(organizationId, input);
        var response = tenantWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            value = "/tenants/{tenantId}", version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantResponse getTenant(@PathVariable UUID tenantId) {
        var output = tenantUseCase.getTenant(tenantId);

        return tenantWebMapper.toResponse(output);
    }

    @GetMapping(
            value = "/organizations/{organizationId}/tenants",
            version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<TenantResponse> getTenants(@PathVariable UUID organizationId) {
        var output = tenantUseCase.getTenants(organizationId);

        return tenantWebMapper.toResponse(output);
    }

    @PutMapping(
            value = "/tenants/{tenantId}",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantResponse updateTenant(
            @PathVariable UUID tenantId, @Valid @RequestBody TenantUpdateRequest request) {
        var input = tenantWebMapper.toInput(request);
        var output = tenantUseCase.updateTenant(tenantId, input);

        return tenantWebMapper.toResponse(output);
    }

    @PatchMapping(
            value = "/tenants/{tenantId}/status",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantResponse changeTenantStatus(
            @PathVariable UUID tenantId, @Valid @RequestBody TenantStatusRequest request) {
        var output = tenantUseCase.changeTenantStatus(tenantId, request.status());

        return tenantWebMapper.toResponse(output);
    }

    @DeleteMapping(value = "/tenants/{tenantId}", version = "1+")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID tenantId) {
        tenantUseCase.deleteTenant(tenantId);

        return ResponseEntity.noContent().build();
    }
}
