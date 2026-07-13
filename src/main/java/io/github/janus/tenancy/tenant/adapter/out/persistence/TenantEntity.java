package io.github.janus.tenancy.tenant.adapter.out.persistence;

import io.github.janus.common.AbstractEntity;
import io.github.janus.tenancy.customer.CustomerEntity;
import io.github.janus.tenancy.tenant.domain.type.EnvironmentTag;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tenant")
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(name = "friendly_name")
    private String friendlyName;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "support_email")
    private String supportEmail;

    @Column(name = "enviroment_tag")
    @Enumerated(EnumType.STRING)
    private EnvironmentTag environmentTag;


}
