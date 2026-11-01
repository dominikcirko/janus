package janus.tenancy.organization.adapter.in.web.mapper;

import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipResponse;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipUpdateRequest;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationMembershipWebMapper {

    OrganizationMembershipCreateInput toInput(OrganizationMembershipRequest request);
    OrganizationMembershipUpdateInput toInput(OrganizationMembershipUpdateRequest request);
    OrganizationMembershipResponse toResponse(OrganizationMembershipOutput output);
    List<OrganizationMembershipResponse> toResponse(List<OrganizationMembershipOutput> output);
}
