package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantEnvironment;

public record TenantInput(
        String name,
        String slug,
        TenantEnvironment environment,
        String issuer
) {
}
