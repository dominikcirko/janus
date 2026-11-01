package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class Organization {

    UUID id;
    Instant createdAt;
    Instant updatedAt;
    String name;
    String slug;
    OrganizationStatus status;

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }
}
