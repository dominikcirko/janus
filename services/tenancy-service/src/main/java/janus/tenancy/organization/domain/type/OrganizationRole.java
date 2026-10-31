package janus.tenancy.organization.domain.type;

import java.util.EnumSet;
import java.util.Set;

public enum OrganizationRole {
    OWNER(EnumSet.allOf(OrganizationPermission.class)),
    ADMIN(EnumSet.of(
            OrganizationPermission.MANAGE_TENANTS,
            OrganizationPermission.MANAGE_MEMBERS,
            OrganizationPermission.MANAGE_SETTINGS
    ));

    private final Set<OrganizationPermission> permissions;

    OrganizationRole(Set<OrganizationPermission> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public boolean hasPermission(OrganizationPermission permission) {
        return permissions.contains(permission);
    }
}
