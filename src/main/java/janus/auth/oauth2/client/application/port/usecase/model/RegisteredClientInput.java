package janus.auth.oauth2.client.application.port.usecase.model;

import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;

import java.util.Set;

public record RegisteredClientInput(
        Set<String> redirectUris,
        ClientAuthenticationMethod tokenEndpointAuthMethod,
        Set<GrantType> grantTypes,
        Set<ResponseType> responseTypes,
        String clientName,
        String clientUri,
        String scope,
        String description
) {
}
