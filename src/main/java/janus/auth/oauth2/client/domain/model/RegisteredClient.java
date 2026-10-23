package janus.auth.oauth2.client.domain.model;

import jakarta.persistence.*;
import janus.auth.oauth2.client.domain.type.ClientType;
import janus.common.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

//https://datatracker.ietf.org/doc/html/rfc6749#section-2
@Entity
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registered_client")
public class RegisteredClient extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType type;

    @Column(name = "redirection_uri", nullable = false)
    private List<String> redirectionUri;

    @Column(name = "application_name")
    private String applicationName;

    private String website;
    private String description;

}
