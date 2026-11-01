package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantEnvironment;

public record TenantUpdateRequest(
        @NotBlank String name,
        @NotBlank String slug,
        @NotNull TenantEnvironment environment
) {
}
