package janus.tenancy.organization.application.mapper;

import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Organization toOrganization(OrganizationInput input);

    @Mapping(target = "id", source = "organization.id")
    @Mapping(target = "createdAt", source = "organization.createdAt")
    @Mapping(target = "updatedAt", source = "organization.updatedAt")
    @Mapping(target = "name", source = "input.name")
    @Mapping(target = "slug", source = "input.slug")
    @Mapping(target = "status", source = "organization.status")
    Organization updateOrganization(Organization organization, OrganizationInput input);

    @Mapping(target = "id", source = "organization.id")
    @Mapping(target = "createdAt", source = "organization.createdAt")
    @Mapping(target = "updatedAt", source = "organization.updatedAt")
    @Mapping(target = "name", source = "organization.name")
    @Mapping(target = "slug", source = "organization.slug")
    @Mapping(target = "status", source = "status")
    Organization changeStatus(Organization organization, OrganizationStatus status);

    OrganizationOutput toOutput(Organization organization);
}
