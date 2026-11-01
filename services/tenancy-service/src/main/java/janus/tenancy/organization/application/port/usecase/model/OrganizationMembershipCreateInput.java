package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationRole;

import java.util.UUID;

public record OrganizationMembershipCreateInput(UUID userId, OrganizationRole role) { }
