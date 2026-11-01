package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;
import java.util.UUID;

public record TenantMembershipCreateInput(
        UUID userId,
        TenantRole role,
        Map<String, Object> metadata
) {
}
