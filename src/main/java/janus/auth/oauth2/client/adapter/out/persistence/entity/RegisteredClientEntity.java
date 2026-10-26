package janus.auth.oauth2.client.adapter.out.persistence.entity;

import jakarta.persistence.*;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.ClientStatus;
import janus.auth.oauth2.client.domain.type.ClientType;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
import janus.common.AbstractEntity;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Persists the metadata associated with a registered OAuth client.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
 */
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "registered_client")
public class RegisteredClientEntity extends AbstractEntity {

    //todo add multitenancy

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 32)
    private ClientType type;

    @Builder.Default
    @ElementCollection
    @Column(name = "redirect_uri", nullable = false, columnDefinition = "TEXT")
    @CollectionTable(
            name = "registered_client_redirect_uri",
            joinColumns = @JoinColumn(name = "registered_client_id", nullable = false)
    )
    private Set<String> redirectUris = new HashSet<>();

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_id", nullable = false,
            unique = true, updatable = false)
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_authentication_method", nullable = false, length = 64)
    private ClientAuthenticationMethod authenticationMethod;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "client_status", nullable = false, length = 32)
    private ClientStatus status = ClientStatus.ACTIVE;

    @Column(name = "client_secret_hash")
    private String clientSecretHash;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @ElementCollection
    @Builder.Default
    @Column(name = "grant_type", nullable = false, length = 128)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "registered_client_grant_type",
            joinColumns = @JoinColumn(name = "registered_client_id", nullable = false)
    )
    private Set<GrantType> grantTypes = new HashSet<>();

    @ElementCollection
    @Builder.Default
    @Column(name = "scope", nullable = false)
    @CollectionTable(
            name = "registered_client_scope",
            joinColumns = @JoinColumn(name = "registered_client_id", nullable = false)
    )
    private Set<String> scopes = new HashSet<>();

    @ElementCollection
    @Builder.Default
    @Column(name = "response_type", nullable = false, length = 128)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "registered_client_response_type",
            joinColumns = @JoinColumn(name = "registered_client_id", nullable = false)
    )
    private Set<ResponseType> responseTypes = new HashSet<>();

    @Column(name = "client_uri", columnDefinition = "TEXT")
    private String clientUri;

    @Column(columnDefinition = "TEXT")
    private String description;
}
