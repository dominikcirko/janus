package janus.tenancy.tenant.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.tenancy.tenant.domain.type.TenantStatus;

public record TenantStatusRequest(@NotNull TenantStatus status) {
}
