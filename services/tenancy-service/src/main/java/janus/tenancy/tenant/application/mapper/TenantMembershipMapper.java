package janus.tenancy.tenant.application.mapper;

import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantMembershipMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", source = "tenantId")
    @Mapping(target = "status", constant = "ACTIVE")
    TenantMembership toTenantMembership(UUID tenantId, TenantMembershipCreateInput input);

    @Mapping(target = "id", source = "membership.id")
    @Mapping(target = "tenantId", source = "membership.tenantId")
    @Mapping(target = "userId", source = "membership.userId")
    @Mapping(target = "role", source = "input.role")
    @Mapping(target = "status", source = "membership.status")
    @Mapping(target = "metadata", source = "input.metadata")
    TenantMembership toUpdatedTenantMembership(TenantMembership membership, TenantMembershipUpdateInput input);

    @Mapping(target = "id", source = "membership.id")
    @Mapping(target = "tenantId", source = "membership.tenantId")
    @Mapping(target = "userId", source = "membership.userId")
    @Mapping(target = "role", source = "membership.role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "metadata", source = "membership.metadata")
    TenantMembership withUpdatedStatus(TenantMembership membership, TenantMembershipStatus status);

    TenantMembershipOutput toOutput(TenantMembership membership);
}
