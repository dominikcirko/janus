package janus.tenancy.tenant.adapter.in.web.mapper;

import janus.tenancy.tenant.adapter.in.web.dto.TenantRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantUpdateRequest;
import janus.tenancy.tenant.application.port.usecase.model.TenantInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TenantWebMapper {

    TenantInput toInput(TenantRequest request);
    TenantUpdateInput toInput(TenantUpdateRequest request);
    TenantResponse toResponse(TenantOutput output);
    List<TenantResponse> toResponse(List<TenantOutput> output);
}
