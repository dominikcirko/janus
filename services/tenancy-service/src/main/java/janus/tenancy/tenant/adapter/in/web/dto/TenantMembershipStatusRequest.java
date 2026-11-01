package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;

public record TenantMembershipStatusRequest(@NotNull TenantMembershipStatus status) {
}
