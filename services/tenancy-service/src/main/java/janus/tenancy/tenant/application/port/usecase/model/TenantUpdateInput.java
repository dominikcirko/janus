package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantEnvironment;

public record TenantUpdateInput(
        String name,
        String slug,
        TenantEnvironment environment
) {
}
