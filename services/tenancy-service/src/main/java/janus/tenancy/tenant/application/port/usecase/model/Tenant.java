package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Tenant {

    UUID id;
    UUID organizationId;
    String name;
    String slug;
    TenantEnvironment environment;
    TenantStatus status;
    String issuer;

    public boolean isActive() {
        return status == TenantStatus.ACTIVE;
    }
}
