package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationRole;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class OrganizationMembership {

    UUID id;
    UUID organizationId;
    UUID userId;
    OrganizationRole role;
}
