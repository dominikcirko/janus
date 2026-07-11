package io.github.janus.identity.role.domain.model;

import io.github.janus.identity.permission.domain.model.Permission;
import io.github.janus.tenant.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private String name;
    private UUID tenantId;
    private String description;
    private Set<Permission> permissions;
}
