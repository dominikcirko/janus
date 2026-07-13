package io.github.janus.identity.user.adapter.out.persistence;

import io.github.janus.identity.credential.adapter.out.persistence.CredentialPersistenceMapper;
import io.github.janus.identity.permission.adapter.out.persistence.PermissionPersistenceMapper;
import io.github.janus.identity.role.adapter.out.persistence.RolePersistenceMapper;
import io.github.janus.identity.user.domain.model.User;
import io.github.janus.tenancy.tenant.adapter.out.persistence.TenantPersistenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {
        PermissionPersistenceMapper.class,
        TenantPersistenceMapper.class,
        CredentialPersistenceMapper.class,
        RolePersistenceMapper.class
})
public interface UserPersistenceMapper {

    @Mapping(target = "tenant", ignore = true)
    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);

    List<User> toDomainList(List<UserEntity> users);
    List<UserEntity> toEntityList(List<User> users);
}