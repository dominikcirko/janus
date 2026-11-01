package janus.tenancy.organization.application.port.usecase.model;

import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationOutput(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String name,
        String slug,
        OrganizationStatus status
) {
}
