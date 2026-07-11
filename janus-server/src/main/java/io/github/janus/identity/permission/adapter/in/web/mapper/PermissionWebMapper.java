package io.github.janus.identity.permission.adapter.in.web.mapper;

import io.github.janus.identity.permission.adapter.in.web.dto.PermissionRequest;
import io.github.janus.identity.permission.adapter.in.web.dto.PermissionResponse;
import io.github.janus.identity.permission.domain.model.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PermissionWebMapper {

    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);

    Permission toDomain(PermissionRequest request);
}
