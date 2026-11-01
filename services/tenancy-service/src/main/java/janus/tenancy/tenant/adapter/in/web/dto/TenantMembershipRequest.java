package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;
import java.util.UUID;

public record TenantMembershipRequest(
        @NotNull UUID userId,
        @NotNull TenantRole role,
        @NotNull Map<String, Object> metadata
) {
}
