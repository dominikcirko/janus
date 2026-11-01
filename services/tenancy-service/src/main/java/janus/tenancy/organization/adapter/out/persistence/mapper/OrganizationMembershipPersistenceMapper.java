package janus.tenancy.organization.adapter.out.persistence.mapper;

import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationMembershipEntity;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationMembershipPersistenceMapper {

    @Mapping(target = "id", source = "membership.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", source = "organization")
    @Mapping(target = "userId", source = "membership.userId")
    @Mapping(target = "role", source = "membership.role")
    OrganizationMembershipEntity toEntity(OrganizationMembership membership, OrganizationEntity organization);

    @Mapping(target = "organizationId", source = "organization.id")
    OrganizationMembership toMembership(OrganizationMembershipEntity entity);
}
