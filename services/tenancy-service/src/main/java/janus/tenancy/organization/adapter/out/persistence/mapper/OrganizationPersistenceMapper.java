package janus.tenancy.organization.adapter.out.persistence.mapper;

import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrganizationEntity toEntity(Organization organization);

    Organization toOrganization(OrganizationEntity entity);
}
