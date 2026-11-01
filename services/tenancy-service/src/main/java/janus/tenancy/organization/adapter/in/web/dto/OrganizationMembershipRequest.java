package janus.tenancy.organization.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.organization.domain.type.OrganizationRole;

import java.util.UUID;

public record OrganizationMembershipRequest(
        @NotNull UUID userId,
        @NotNull OrganizationRole role
) {
}
