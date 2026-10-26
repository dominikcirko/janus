package janus.auth.oauth2.client.adapter.in.web.mapper;

import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientRequest;
import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientResponse;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RegisteredClientWebMapper {

    RegisteredClientInput toInput(RegisteredClientRequest request);

    RegisteredClientResponse toResponse(RegisteredClientOutput output);
}
