package janus.common;

import java.util.UUID;

public final class TenantContext {

    private static final ScopedValue<UUID> TENANT_ID = ScopedValue.newInstance();

    private TenantContext() {}

    public static UUID currentTenantId() {
        return TENANT_ID.get();
    }

    public static void runWithTenant(UUID tenantId, Runnable action) {
        ScopedValue.where(TENANT_ID, tenantId).run(action);
    }
}