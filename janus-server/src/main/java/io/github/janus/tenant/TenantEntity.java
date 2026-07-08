package io.github.janus.tenant;

import io.github.janus.common.AbstractEntity;
import io.github.janus.customer.CustomerEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerId;

    @Column(name = "friendly_name")
    private String friendlyName;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "support_email")
    private String supportEmail;

    @Column(name = "enviroment_tag")
    @Enumerated(EnumType.STRING)
    private EnviromentTag enviromentTag;


}
