package io.github.janus.identity;

import io.github.janus.common.AbstractEntity;
import io.github.janus.tenant.TenantEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "role")
@EqualsAndHashCode(callSuper = true)
public class RoleEntity extends AbstractEntity {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant; // tenant level roles - tenant admins customize them per tenant

}
