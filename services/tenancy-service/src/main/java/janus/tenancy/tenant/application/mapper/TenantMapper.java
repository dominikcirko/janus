package janus.tenancy.tenant.application.mapper;

import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import janus.tenancy.tenant.application.port.usecase.model.TenantInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantUpdateInput;
import janus.tenancy.tenant.domain.type.TenantStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", source = "organizationId")
    @Mapping(target = "status", constant = "ACTIVE")
    Tenant toTenant(UUID organizationId, TenantInput input);

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "organizationId", source = "tenant.organizationId")
    @Mapping(target = "name", source = "input.name")
    @Mapping(target = "slug", source = "input.slug")
    @Mapping(target = "environment", source = "input.environment")
    @Mapping(target = "status", source = "tenant.status")
    @Mapping(target = "issuer", source = "tenant.issuer")
    Tenant toUpdatedTenant(Tenant tenant, TenantUpdateInput input);

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "organizationId", source = "tenant.organizationId")
    @Mapping(target = "name", source = "tenant.name")
    @Mapping(target = "slug", source = "tenant.slug")
    @Mapping(target = "environment", source = "tenant.environment")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "issuer", source = "tenant.issuer")
    Tenant withUpdatedStatus(Tenant tenant, TenantStatus status);

    TenantOutput toOutput(Tenant tenant);
}
