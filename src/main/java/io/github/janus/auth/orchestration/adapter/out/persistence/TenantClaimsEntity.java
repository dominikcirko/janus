package io.github.janus.auth.orchestration.adapter.out.persistence;


import io.github.janus.common.AbstractEntity;
import io.github.janus.tenancy.tenant.adapter.out.persistence.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Table(name = "tenant_claims")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantClaimsEntity extends AbstractEntity {

    @Column(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TenantEntity tenant;

    //todo: find valid way to store mandatory & custom claims used to construct jwt in tenant
    //private List<String> customTenantClaims; iss, exp...

}
