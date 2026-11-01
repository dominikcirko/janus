package janus.tenancy.tenant.adapter.out.persistence.mapper;

import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.tenant.adapter.out.persistence.entity.TenantEntity;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantPersistenceMapper {

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", source = "organization")
    @Mapping(target = "name", source = "tenant.name")
    @Mapping(target = "slug", source = "tenant.slug")
    @Mapping(target = "environment", source = "tenant.environment")
    @Mapping(target = "status", source = "tenant.status")
    @Mapping(target = "issuer", source = "tenant.issuer")
    TenantEntity toEntity(Tenant tenant, OrganizationEntity organization);

    @Mapping(target = "organizationId", source = "organization.id")
    Tenant toTenant(TenantEntity entity);
}
