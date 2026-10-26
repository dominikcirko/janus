package janus.auth.oauth2.client.application.port.usecase.model;

import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;

import java.util.Set;

/**
 * Application representation of client registration metadata.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
 */
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
