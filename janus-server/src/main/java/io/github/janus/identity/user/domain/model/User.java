package io.github.janus.identity.user.domain.model;

import io.github.janus.identity.credential.domain.model.Credential;
import io.github.janus.identity.role.domain.model.Role;
import io.github.janus.tenant.domain.model.Tenant;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class User {

    private UUID id;
    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private boolean verified;
    private Set<Role> roles;
    private Tenant tenant;
    private List<Credential> credentials;

}
