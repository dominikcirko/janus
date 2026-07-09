package io.github.janus.identity.domain.model;

import io.github.janus.identity.adapter.out.persistence.entity.CredentialsEntity;
import io.github.janus.identity.adapter.out.persistence.entity.RoleEntity;
import io.github.janus.tenant.TenantEntity;

import java.util.List;
import java.util.Set;

public class User {

    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private Set<RoleEntity> roles;
    private TenantEntity tenant;
    private List<CredentialsEntity> credentials;

}
