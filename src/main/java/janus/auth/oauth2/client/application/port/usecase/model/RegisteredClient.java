package janus.auth.oauth2.client.application.port.usecase.model;

import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.ClientStatus;
import janus.auth.oauth2.client.domain.type.ClientType;
import janus.auth.oauth2.common.type.GrantType;
import janus.auth.oauth2.common.type.ResponseType;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Internal representation of a registered OAuth client used by application services.
 */
@Value
@Builder
public class RegisteredClient {

    UUID id;
    Instant createdAt;
    Instant updatedAt;
    ClientType type;
    Set<String> redirectUris;
    String clientName;
    String clientId;
    ClientAuthenticationMethod authenticationMethod;
    ClientStatus status;
    String clientSecretHash;
    Instant clientSecretExpiresAt;
    Set<GrantType> grantTypes;
    Set<String> scopes;
    Set<ResponseType> responseTypes;
    String clientUri;
    String description;
}
