package io.github.janus.tenant.domain.model;

import io.github.janus.tenant.domain.type.EnvironmentTag;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Tenant {

    private UUID id;
    private UUID customerId;
    private String friendlyName;
    private String logoUrl;
    private String supportEmail;
    private EnvironmentTag environmentTag;


}
