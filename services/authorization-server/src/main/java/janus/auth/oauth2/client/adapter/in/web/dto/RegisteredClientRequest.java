package janus.auth.oauth2.client.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.common.type.GrantType;
import janus.auth.oauth2.common.type.ResponseType;

import java.util.Set;

/**
 * Client metadata accepted by the dynamic registration endpoint.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.1">RFC 7591, Section 3.1</a>
 */
public record RegisteredClientRequest(
        @JsonProperty("redirect_uris")
        Set<@NotBlank String> redirectUris,

        @JsonProperty("token_endpoint_auth_method")
        ClientAuthenticationMethod tokenEndpointAuthMethod,

        @JsonProperty("grant_types")
        Set<GrantType> grantTypes,

        @JsonProperty("response_types")
        Set<ResponseType> responseTypes,

        @Size(max = 255)
        @JsonProperty("client_name")
        String clientName,

        @JsonProperty("client_uri")
        String clientUri,

        String scope,
        String description
) {

    /**
     * Applies the RFC defaults for omitted authentication, grant, and response types.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
     */
    public RegisteredClientRequest {
        if (redirectUris == null) {
            redirectUris = Set.of();
        } else {
            redirectUris = Set.copyOf(redirectUris);
        }

        if (tokenEndpointAuthMethod == null) {
            tokenEndpointAuthMethod = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        if (grantTypes == null) {
            grantTypes = Set.of(GrantType.AUTHORIZATION_CODE);
        } else {
            grantTypes = Set.copyOf(grantTypes);
        }

        if (responseTypes == null) {
            responseTypes = Set.of(ResponseType.CODE);
        } else {
            responseTypes = Set.copyOf(responseTypes);
        }
    }
}
