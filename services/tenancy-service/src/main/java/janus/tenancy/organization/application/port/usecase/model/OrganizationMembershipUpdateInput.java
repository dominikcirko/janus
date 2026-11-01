package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationRole;

public record OrganizationMembershipUpdateInput(OrganizationRole role) { }
