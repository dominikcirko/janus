package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantEnvironment;

public record TenantRequest(
        @NotBlank String name,
        @NotBlank String slug,
        @NotNull TenantEnvironment environment,
        @NotBlank String issuer
) {}
