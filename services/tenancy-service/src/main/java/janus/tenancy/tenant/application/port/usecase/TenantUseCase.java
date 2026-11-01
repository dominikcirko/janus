package janus.tenancy.tenant.application.port.usecase;

import janus.tenancy.tenant.application.port.usecase.model.TenantCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantUpdateInput;
import janus.tenancy.tenant.domain.type.TenantStatus;

import java.util.List;
import java.util.UUID;

public interface TenantUseCase {

    TenantOutput createTenant(UUID organizationId, TenantCreateInput input);
    TenantOutput getTenant(UUID tenantId);
    List<TenantOutput> getTenants(UUID organizationId);
    TenantOutput updateTenant(UUID tenantId, TenantUpdateInput input);
    TenantOutput changeTenantStatus(UUID tenantId, TenantStatus status);
    void deleteTenant(UUID tenantId);
}
