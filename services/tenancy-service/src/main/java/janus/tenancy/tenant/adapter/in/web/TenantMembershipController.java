package janus.tenancy.tenant.adapter.in.web;

import jakarta.validation.Valid;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipStatusRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipUpdateRequest;
import janus.tenancy.tenant.adapter.in.web.mapper.TenantMembershipWebMapper;
import janus.tenancy.tenant.application.port.usecase.TenantMembershipUseCase;
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
public class TenantMembershipController {

    private final TenantMembershipUseCase tenantMembershipUseCase;
    private final TenantMembershipWebMapper tenantMembershipWebMapper;

    @PostMapping(
            value = "/tenants/{tenantId}/memberships",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TenantMembershipResponse> createTenantMembership(
            @PathVariable UUID tenantId, @Valid @RequestBody TenantMembershipRequest request) {
        var input = tenantMembershipWebMapper.toInput(request);
        var output = tenantMembershipUseCase.createTenantMembership(tenantId, input);
        var response = tenantMembershipWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            value = "/tenant-memberships/{membershipId}",
            version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantMembershipResponse getTenantMembership(@PathVariable UUID membershipId) {
        var output = tenantMembershipUseCase.getTenantMembership(membershipId);

        return tenantMembershipWebMapper.toResponse(output);
    }

    @GetMapping(
            value = "/tenants/{tenantId}/memberships",
            version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<TenantMembershipResponse> getTenantMemberships(@PathVariable UUID tenantId) {
        var output = tenantMembershipUseCase.getTenantMemberships(tenantId);

        return tenantMembershipWebMapper.toResponse(output);
    }

    @PutMapping(
            value = "/tenant-memberships/{membershipId}",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantMembershipResponse updateTenantMembership(
            @PathVariable UUID membershipId, @Valid @RequestBody TenantMembershipUpdateRequest request) {
        var input = tenantMembershipWebMapper.toInput(request);
        var output = tenantMembershipUseCase.updateTenantMembership(membershipId, input);

        return tenantMembershipWebMapper.toResponse(output);
    }

    @PatchMapping(
            value = "/tenant-memberships/{membershipId}/status",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TenantMembershipResponse changeTenantMembershipStatus(
            @PathVariable UUID membershipId, @Valid @RequestBody TenantMembershipStatusRequest request) {
        var output = tenantMembershipUseCase.changeTenantMembershipStatus(membershipId, request.status());

        return tenantMembershipWebMapper.toResponse(output);
    }

    @DeleteMapping(value = "/tenant-memberships/{membershipId}", version = "1+")
    public ResponseEntity<Void> deleteTenantMembership(@PathVariable UUID membershipId) {
        tenantMembershipUseCase.deleteTenantMembership(membershipId);

        return ResponseEntity.noContent().build();
    }
}
