package io.github.janus.identity.user.adapter.out.persistence;

import io.github.janus.common.AbstractEntity;
import io.github.janus.identity.credential.adapter.out.persistence.CredentialEntity;
import io.github.janus.identity.role.adapter.out.persistence.RoleEntity;
import io.github.janus.tenant.TenantEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "user")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractEntity {

    @Column
    private String username;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "given_name")
    private String givenName;

    @Column
    private String email;

    @Column
    private boolean verified;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;

    @OneToMany
    @JoinColumn(name = "credentials_id")
    private List<CredentialEntity> credentials;

}
