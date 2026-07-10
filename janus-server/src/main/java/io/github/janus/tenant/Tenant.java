package io.github.janus.tenant;

import io.github.janus.customer.Customer;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Tenant {

    private UUID id;
    private Customer customerId;
    private String friendlyName;
    private String logoUrl;
    private String supportEmail;
    private EnvironmentTag environmentTag;


}
