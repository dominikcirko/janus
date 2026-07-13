package io.github.janus.identity.permission.adapter.out.persistence;

import io.github.janus.identity.permission.domain.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PermissionPersistenceMapper {

    Permission toDomain(PermissionEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PermissionEntity toEntity(Permission permission);
}
