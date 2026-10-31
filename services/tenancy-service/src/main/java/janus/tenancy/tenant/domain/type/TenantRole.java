package janus.tenancy.tenant.domain.type;

import java.util.EnumSet;
import java.util.Set;

public enum TenantRole {
    OWNER(EnumSet.allOf(TenantPermission.class)),
    ADMIN(EnumSet.of(
            TenantPermission.MANAGE_CLIENTS,
            TenantPermission.MANAGE_MEMBERS,
            TenantPermission.MANAGE_SETTINGS
    )),
    MEMBER(EnumSet.noneOf(TenantPermission.class));

    private final Set<TenantPermission> permissions;

    TenantRole(Set<TenantPermission> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public boolean hasPermission(TenantPermission permission) {
        return permissions.contains(permission);
    }
}
