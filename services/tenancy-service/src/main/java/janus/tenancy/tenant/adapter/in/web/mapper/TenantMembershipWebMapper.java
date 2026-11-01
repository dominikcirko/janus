package janus.tenancy.tenant.adapter.in.web.mapper;

import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipUpdateRequest;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantMembershipWebMapper {

    TenantMembershipCreateInput toInput(TenantMembershipRequest request);
    TenantMembershipUpdateInput toInput(TenantMembershipUpdateRequest request);
    TenantMembershipResponse toResponse(TenantMembershipOutput output);
    List<TenantMembershipResponse> toResponse(List<TenantMembershipOutput> output);
}
