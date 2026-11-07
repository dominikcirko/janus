package janus.common;

import java.util.UUID;

public final class TenantContext {

    private static final ScopedValue<UUID> TENANT_ID = ScopedValue.newInstance();

    private TenantContext() {}

    public static UUID currentTenantId() {
        return TENANT_ID.get();
    }

    public static <R, X extends Throwable> R callWithTenant(
            UUID tenantId, ScopedValue.CallableOp<R, X> action) throws X {
        return ScopedValue.where(TENANT_ID, tenantId).call(action);
    }
}
