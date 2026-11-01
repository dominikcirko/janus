package janus.tenancy.tenant.adapter.out.persistence.mapper;

import janus.tenancy.tenant.adapter.out.persistence.entity.TenantEntity;
import janus.tenancy.tenant.adapter.out.persistence.entity.TenantMembershipEntity;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantMembershipPersistenceMapper {

    @Mapping(target = "id", source = "membership.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", source = "tenant")
    @Mapping(target = "userId", source = "membership.userId")
    @Mapping(target = "role", source = "membership.role")
    @Mapping(target = "status", source = "membership.status")
    @Mapping(target = "metadata", source = "membership.metadata")
    TenantMembershipEntity toEntity(TenantMembership membership, TenantEntity tenant);

    @Mapping(target = "tenantId", source = "tenant.id")
    TenantMembership toMembership(TenantMembershipEntity entity);
}
