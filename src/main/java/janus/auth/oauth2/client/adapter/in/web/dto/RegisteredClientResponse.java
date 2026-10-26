package janus.auth.oauth2.client.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;

import java.util.Set;

/**
 * Successful registration response containing issued credentials and registered metadata.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegisteredClientResponse(
        @JsonProperty("client_id")
        String clientId,

        @JsonProperty("client_secret")
        String clientSecret,

        @JsonProperty("client_id_issued_at")
        Long clientIdIssuedAt,

        @JsonProperty("client_secret_expires_at")
        Long clientSecretExpiresAt,

        @JsonProperty("redirect_uris")
        Set<String> redirectUris,

        @JsonProperty("token_endpoint_auth_method")
        ClientAuthenticationMethod tokenEndpointAuthMethod,

        @JsonProperty("grant_types")
        Set<GrantType> grantTypes,

        @JsonProperty("response_types")
        Set<ResponseType> responseTypes,

        @JsonProperty("client_name")
        String clientName,

        @JsonProperty("client_uri")
        String clientUri,

        String scope,
        String description
) {
}
