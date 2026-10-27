package janus.auth.oauth2.authorization.adapter.in.web.mapper;

import janus.auth.oauth2.authorization.adapter.in.web.dto.AuthorizationRequest;
import janus.auth.oauth2.authorization.adapter.in.web.dto.AuthorizationResponse;
import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationInput;
import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationOutput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthorizationWebMapper {

    AuthorizationInput toInput(AuthorizationRequest request);

    AuthorizationResponse toResponse(AuthorizationOutput output);
}
