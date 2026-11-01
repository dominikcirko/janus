package janus.tenancy.organization.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationRequest(
        @NotBlank String name,
        @NotBlank String slug
) {
}
