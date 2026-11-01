package janus.tenancy.tenant.adapter.in.web.dto;

import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;
import java.util.UUID;

public record TenantMembershipResponse(
        UUID id,
        UUID tenantId,
        UUID userId,
        TenantRole role,
        TenantMembershipStatus status,
        Map<String, Object> metadata
) {
}
