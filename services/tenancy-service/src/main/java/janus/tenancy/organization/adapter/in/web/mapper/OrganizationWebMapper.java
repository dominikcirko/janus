package janus.tenancy.organization.adapter.in.web.mapper;

import janus.tenancy.organization.adapter.in.web.dto.OrganizationRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationResponse;
import janus.tenancy.organization.application.port.usecase.model.OrganizationInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrganizationWebMapper {
    OrganizationInput toInput(OrganizationRequest request);
    OrganizationResponse toResponse(OrganizationOutput output);
    List<OrganizationResponse> toResponse(List<OrganizationOutput> output);
}
