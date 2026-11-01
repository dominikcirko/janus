package janus.tenancy.tenant.application.port.usecase;

import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;

import java.util.List;
import java.util.UUID;

public interface TenantMembershipUseCase {

    TenantMembershipOutput createTenantMembership(UUID tenantId, TenantMembershipCreateInput input);
    TenantMembershipOutput getTenantMembership(UUID membershipId);
    List<TenantMembershipOutput> getTenantMemberships(UUID tenantId);
    TenantMembershipOutput updateTenantMembership(UUID membershipId, TenantMembershipUpdateInput input);
    TenantMembershipOutput changeTenantMembershipStatus(UUID membershipId, TenantMembershipStatus status);
    void deleteTenantMembership(UUID membershipId);
}
