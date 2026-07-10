package io.github.janus.identity.adapter.out.persistence.entity;

import io.github.janus.common.AbstractEntity;
import io.github.janus.tenant.TenantEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Data
@Table(name = "role")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleEntity extends AbstractEntity {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant; // tenant level roles - tenant admins customize them per tenant

    @Column
    private String description;

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions;

}
