package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;
import java.util.UUID;

public record TenantMembershipOutput(
        UUID id,
        UUID tenantId,
        UUID userId,
        TenantRole role,
        TenantMembershipStatus status,
        Map<String, Object> metadata
) {
}
