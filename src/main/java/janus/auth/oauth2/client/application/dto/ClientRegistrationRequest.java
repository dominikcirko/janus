package janus.auth.oauth2.client.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;

import java.util.Set;

public record ClientRegistrationRequest(
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

    public ClientRegistrationRequest {
        redirectUris = redirectUris == null
                ? Set.of()
                : Set.copyOf(redirectUris);
        tokenEndpointAuthMethod = tokenEndpointAuthMethod == null
                ? ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                : tokenEndpointAuthMethod;
        grantTypes = grantTypes == null
                ? Set.of(GrantType.AUTHORIZATION_CODE)
                : Set.copyOf(grantTypes);
        responseTypes = responseTypes == null
                ? Set.of(ResponseType.CODE)
                : Set.copyOf(responseTypes);
    }
}
