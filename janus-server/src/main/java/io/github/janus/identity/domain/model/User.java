package io.github.janus.identity.domain.model;

import io.github.janus.tenant.Tenant;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class User {

    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private boolean verified;
    private Set<Role> roles;
    private Tenant tenant;
    private List<Credentials> credentials;

}
