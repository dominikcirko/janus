package io.github.janus.identity.user.adapter.out.persistence;

import io.github.janus.identity.credential.adapter.out.persistence.CredentialEntity;
import io.github.janus.identity.permission.adapter.out.persistence.PermissionPersistenceMapper;
import io.github.janus.identity.role.adapter.out.persistence.RoleEntity;
import io.github.janus.identity.credential.domain.model.Credential;
import io.github.janus.identity.role.domain.model.Role;
import io.github.janus.identity.user.domain.model.User;
import io.github.janus.tenant.Tenant;
import io.github.janus.tenant.TenantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = { PermissionPersistenceMapper.class })
public interface UserPersistenceMapper {

    @Mapping(target = "tenant", ignore = true)
    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);

    List<User> toDomainList(List<UserEntity> users);
    List<UserEntity> toEntityList(List<User> users);

    @Mapping(target = "tenantId", source = "tenant.id")
    Role toDomain(RoleEntity roleEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    RoleEntity toEntity(Role role);

    @Mapping(target = "customerId", ignore = true)
    Tenant toDomain(TenantEntity tenantEntity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    TenantEntity toEntity(Tenant tenant);

    @Mapping(target = "userId", source = "user.id")
    Credential toDomain(CredentialEntity credentialEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    CredentialEntity toEntity(Credential credential);
}