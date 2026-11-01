package janus.tenancy.organization.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.organization.domain.type.OrganizationRole;

public record OrganizationMembershipUpdateRequest(@NotNull OrganizationRole role) { }
