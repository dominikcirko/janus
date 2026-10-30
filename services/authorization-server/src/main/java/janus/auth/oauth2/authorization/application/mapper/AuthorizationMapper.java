package janus.auth.oauth2.authorization.application.mapper;

import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationInput;
import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationOutput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.net.URI;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthorizationMapper {

    @Mapping(target = "state", source = "input.state")
    AuthorizationOutput toOutput(AuthorizationInput input, URI validatedRedirectUri, String code);
}
