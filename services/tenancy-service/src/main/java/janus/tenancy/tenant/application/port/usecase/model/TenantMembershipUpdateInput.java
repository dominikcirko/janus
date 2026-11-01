package janus.tenancy.tenant.application.port.usecase.model;

import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;

public record TenantMembershipUpdateInput(TenantRole role, Map<String, Object> metadata) { }
