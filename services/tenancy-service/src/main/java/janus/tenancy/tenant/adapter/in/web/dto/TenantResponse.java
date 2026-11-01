package janus.tenancy.tenant.adapter.in.web.dto;

import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantStatus;

import java.util.UUID;

public record TenantResponse(
        UUID id,
        UUID organizationId,
        String name,
        String slug,
        TenantEnvironment environment,
        TenantStatus status,
        String issuer
) {}
