package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;

public record TenantMembershipUpdateRequest(
        @NotNull TenantRole role,
        @NotNull Map<String, Object> metadata
) {
}
