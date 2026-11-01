package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantRole;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class TenantMembership {

    UUID id;
    UUID tenantId;
    UUID userId;
    TenantRole role;
    TenantMembershipStatus status;
    Map<String, Object> metadata;

    public boolean isActive() {
        return status == TenantMembershipStatus.ACTIVE;
    }
}
