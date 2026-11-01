package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantStatus;

import java.util.UUID;

public record TenantOutput(
        UUID id,
        UUID organizationId,
        String name,
        String slug,
        TenantEnvironment environment,
        TenantStatus status,
        String issuer
) {
}
