package io.github.janus.identity.permission.application.port.in.mapper;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;
import io.github.janus.identity.permission.adapter.out.persistence.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PermissionApplicationMapper {

    PermissionResponse toResponse(PermissionEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PermissionEntity toEntity(PermissionRequest permission);

}
