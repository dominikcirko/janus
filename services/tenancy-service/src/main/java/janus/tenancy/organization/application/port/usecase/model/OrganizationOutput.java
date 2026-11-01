package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.util.UUID;

public record OrganizationOutput(
        UUID id,
        String name,
        String slug,
        OrganizationStatus status
) {
}
