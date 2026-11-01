package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Organization {

    UUID id;
    String name;
    String slug;
    OrganizationStatus status;

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }
}
