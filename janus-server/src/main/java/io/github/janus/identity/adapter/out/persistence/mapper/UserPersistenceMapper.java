package io.github.janus.identity.adapter.out.persistence.mapper;

import io.github.janus.identity.adapter.out.persistence.entity.CredentialsEntity;
import io.github.janus.identity.adapter.out.persistence.entity.RoleEntity;
import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;
import io.github.janus.identity.domain.model.Credentials;
import io.github.janus.identity.domain.model.Role;
import io.github.janus.identity.domain.model.User;
import io.github.janus.tenant.Tenant;
import io.github.janus.tenant.TenantEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserPersistenceMapper {

    // mapper for Domain object -> entity
    UserEntity toEntity(User user);
    User toDomain(Optional<UserEntity> userEntity);
    List<User> toDomainList(List<UserEntity> users);
    List<UserEntity> toEntityList(List<User> users);
    Role toDomain(RoleEntity roleEntity);
    RoleEntity toEntity(Role role);
    Tenant toDomain(TenantEntity tenantEntity);
    TenantEntity toEntity(Tenant tenant);
    Credentials toDomain(CredentialsEntity credentialsEntity);
    CredentialsEntity toEntity(Credentials credentials);
}
