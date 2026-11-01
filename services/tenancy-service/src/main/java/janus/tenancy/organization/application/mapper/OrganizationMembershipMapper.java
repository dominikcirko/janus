package janus.tenancy.organization.application.mapper;

import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationMembershipMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", source = "organizationId")
    OrganizationMembership toOrganizationMembership(UUID organizationId, OrganizationMembershipCreateInput input);

    @Mapping(target = "id", source = "membership.id")
    @Mapping(target = "organizationId", source = "membership.organizationId")
    @Mapping(target = "userId", source = "membership.userId")
    @Mapping(target = "role", source = "input.role")
    OrganizationMembership toUpdatedOrganizationMembership(OrganizationMembership membership, OrganizationMembershipUpdateInput input);

    OrganizationMembershipOutput toOutput(OrganizationMembership membership);
}
