package io.github.janus.identity.user.adapter.out.persistence;

import io.github.janus.identity.credential.adapter.out.persistence.CredentialEntity;
import io.github.janus.identity.role.adapter.out.persistence.RoleEntity;
import io.github.janus.identity.credential.domain.model.Credential;
import io.github.janus.identity.role.domain.model.Role;
import io.github.janus.identity.user.domain.model.User;
import io.github.janus.tenant.Tenant;
import io.github.janus.tenant.TenantEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserPersistenceMapper {

    // mapper for Domain object -> entity
    UserEntity toEntity(User user);
    User toDomain(UserEntity userEntity);
    List<User> toDomainList(List<UserEntity> users);
    List<UserEntity> toEntityList(List<User> users);
    Role toDomain(RoleEntity roleEntity);
    RoleEntity toEntity(Role role);
    Tenant toDomain(TenantEntity tenantEntity);
    TenantEntity toEntity(Tenant tenant);
    Credential toDomain(CredentialEntity credentialEntity);
    CredentialEntity toEntity(Credential credential);
}
