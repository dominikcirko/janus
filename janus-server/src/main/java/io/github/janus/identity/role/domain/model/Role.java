package io.github.janus.identity.role.domain.model;

import io.github.janus.identity.permission.domain.model.Permission;
import io.github.janus.tenant.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private String name;
    private Tenant tenant;
    private String description;
    private boolean isSystemRole;
    private String type;
    private Set<Permission> permissions;

    //todo: check for system/normal role

}
