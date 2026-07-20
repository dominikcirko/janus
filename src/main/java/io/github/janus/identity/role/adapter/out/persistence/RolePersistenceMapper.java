package io.github.janus.identity.role.adapter.out.persistence;

import io.github.janus.identity.role.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RolePersistenceMapper {

    @Mapping(target = "tenantId", source = "tenant.id")
    Role toDomain(RoleEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    RoleEntity toEntity(Role role);
}
