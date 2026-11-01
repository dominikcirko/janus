package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationRole;

import java.util.UUID;

public record OrganizationMembershipOutput(
        UUID id,
        UUID organizationId,
        UUID userId,
        OrganizationRole role
) {
}
