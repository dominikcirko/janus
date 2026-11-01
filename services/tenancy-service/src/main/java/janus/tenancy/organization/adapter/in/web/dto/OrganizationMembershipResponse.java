package janus.tenancy.organization.adapter.in.web.dto;

import janus.tenancy.organization.domain.type.OrganizationRole;

import java.util.UUID;

public record OrganizationMembershipResponse(
        UUID id,
        UUID organizationId,
        UUID userId,
        OrganizationRole role
) {
}
