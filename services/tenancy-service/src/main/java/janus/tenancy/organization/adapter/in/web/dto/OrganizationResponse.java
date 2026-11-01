package janus.tenancy.organization.adapter.in.web.dto;

import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        OrganizationStatus status
) {
}
