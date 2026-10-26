package janus.auth.oauth2.client.application.mapper;

import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
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
    @Mapping(target = "type", source = "input.tokenEndpointAuthMethod", qualifiedByName = "toClientType")
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "authenticationMethod", source = "input.tokenEndpointAuthMethod")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "clientSecretHash", source = "clientSecretHash")
    @Mapping(target = "clientSecretExpiresAt", ignore = true)
    @Mapping(target = "scopes", source = "input.scope", qualifiedByName = "toScopes")
    RegisteredClientEntity toEntity(
            RegisteredClientInput input,
            String clientId,
            String clientSecretHash
    );

    @Mapping(target = "clientSecret", source = "clientSecret")
    @Mapping(target = "clientIdIssuedAt", expression = "java(toEpochSecond(entity.getCreatedAt()))")
    @Mapping(
            target = "clientSecretExpiresAt",
            expression = "java(toExpirationEpochSecond(entity.getClientSecretExpiresAt(), clientSecret))"
    )
    @Mapping(target = "tokenEndpointAuthMethod", source = "entity.authenticationMethod")
    @Mapping(target = "scope", source = "entity.scopes", qualifiedByName = "toScope")
    RegisteredClientOutput toOutput(
            RegisteredClientEntity entity,
            String clientSecret
    );

    /**
     * Derives Janus's internal public/confidential type from the authentication method.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-2.1">RFC 6749, Section 2.1</a>
     */
    @Named("toClientType")
    default ClientType toClientType(ClientAuthenticationMethod authenticationMethod) {
        if (authenticationMethod == ClientAuthenticationMethod.NONE) {
            return ClientType.PUBLIC;
        }

        return ClientType.CONFIDENTIAL;
    }

    /**
     * Converts the space-delimited RFC scope value to the internal set.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     */
    @Named("toScopes")
    default Set<String> toScopes(String scope) {
        if (scope == null) {
            return new HashSet<>();
        }

        return new HashSet<>(Arrays.asList(scope.split(" ")));
    }

    /**
     * Converts internal scopes to the space-delimited registration value.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
     */
    @Named("toScope")
    default String toScope(Set<String> scopes) {
        if (scopes.isEmpty()) {
            return null;
        }

        return String.join(" ", new TreeSet<>(scopes));
    }

    /**
     * Encodes {@code client_id_issued_at} as UTC epoch seconds.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
     */
    default Long toEpochSecond(Instant value) {
        return value.getEpochSecond();
    }

    /**
     * Returns the required secret expiration value, using zero for no expiration.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
     */
    default Long toExpirationEpochSecond(Instant value, String clientSecret) {
        if (clientSecret == null) {
            return null;
        }

        if (value == null) {
            return 0L;
        }

        return value.getEpochSecond();
    }
}
