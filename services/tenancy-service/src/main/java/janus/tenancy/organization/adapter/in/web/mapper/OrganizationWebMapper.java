package janus.tenancy.organization.adapter.in.web.mapper;

import janus.tenancy.organization.adapter.in.web.dto.OrganizationRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationResponse;
import janus.tenancy.organization.application.port.usecase.model.OrganizationCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationWebMapper {
    OrganizationCreateInput toCreateInput(OrganizationRequest request);
    OrganizationUpdateInput toUpdateInput(OrganizationRequest request);
    OrganizationResponse toResponse(OrganizationOutput output);
    List<OrganizationResponse> toResponse(List<OrganizationOutput> output);
}
