package janus.auth.oauth2.client.application.mapper;

import janus.auth.oauth2.client.application.dto.ClientRegistrationRequest;
import janus.auth.oauth2.client.application.dto.ClientRegistrationResponse;
import janus.auth.oauth2.client.domain.model.RegisteredClientEntity;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.ClientType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RegisteredClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", source = "request.tokenEndpointAuthMethod", qualifiedByName = "toClientType")
    @Mapping(target = "applicationName", source = "request.clientName")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "authenticationMethod", source = "request.tokenEndpointAuthMethod")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "clientSecretHash", source = "clientSecretHash")
    @Mapping(target = "clientSecretExpiresAt", ignore = true)
    @Mapping(target = "scopes", source = "request.scope", qualifiedByName = "toScopes")
    RegisteredClientEntity toEntity(
            ClientRegistrationRequest request,
            String clientId,
            String clientSecretHash
    );

    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "clientIdIssuedAt", expression = "java(toEpochSecond(entity.getCreatedAt()))")
    @Mapping(
            target = "clientSecretExpiresAt",
            expression = "java(clientSecret == null ? null : toExpirationEpochSecond(entity.getClientSecretExpiresAt()))"
    )
    @Mapping(target = "tokenEndpointAuthMethod", source = "entity.authenticationMethod")
    @Mapping(target = "clientName", source = "entity.applicationName")
    @Mapping(target = "scope", source = "entity.scopes", qualifiedByName = "toScope")
    ClientRegistrationResponse toResponse(
            RegisteredClientEntity entity,
            String clientSecret
    );

    @Named("toClientType")
    default ClientType toClientType(ClientAuthenticationMethod authenticationMethod) {
        return authenticationMethod == ClientAuthenticationMethod.NONE
                ? ClientType.PUBLIC
                : ClientType.CONFIDENTIAL;
    }

    @Named("toScopes")
    default Set<String> toScopes(String scope) {
        if (scope == null) {
            return new HashSet<>();
        }

        return new HashSet<>(Arrays.asList(scope.split(" ")));
    }

    @Named("toScope")
    default String toScope(Set<String> scopes) {
        if (scopes.isEmpty()) {
            return null;
        }

        return String.join(" ", new TreeSet<>(scopes));
    }

    default Long toEpochSecond(Instant value) {
        return value.getEpochSecond();
    }

    default Long toExpirationEpochSecond(Instant value) {
        return value == null ? 0L : value.getEpochSecond();
    }
}
