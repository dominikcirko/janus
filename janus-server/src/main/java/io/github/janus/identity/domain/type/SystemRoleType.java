package io.github.janus.identity.domain.type;

public enum SystemRoleType {
    SUPER_ADMIN, // admin on customer level and all tenants inside customer
    TENANT_ADMIN, // read/write access inside particular tenant(s)
    VIEWER // readonly access on particular tenant(s)
}
