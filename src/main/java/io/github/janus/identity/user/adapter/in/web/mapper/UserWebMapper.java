package io.github.janus.identity.user.adapter.in.web.mapper;

import io.github.janus.identity.role.adapter.in.web.mapper.RoleWebMapper;
import io.github.janus.identity.user.adapter.in.web.dto.request.UserCreateRequest;
import io.github.janus.identity.user.adapter.in.web.dto.request.UserUpdateRequest;
import io.github.janus.identity.user.adapter.in.web.dto.response.UserGetResponse;
import io.github.janus.identity.user.adapter.in.web.dto.response.UserListResponse;
import io.github.janus.identity.user.adapter.in.web.dto.response.UserUpdateResponse;
import io.github.janus.identity.user.application.port.in.command.UserCreateCommand;
import io.github.janus.identity.user.application.port.in.command.UserUpdateCommand;
import io.github.janus.identity.user.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(uses = { RoleWebMapper.class })
public interface UserWebMapper {

    UserCreateCommand createReqToCommand(UserCreateRequest request, UUID tenantId);
    UserUpdateCommand updateReqToCommand(UserUpdateRequest request, UUID tenantId);

    UserGetResponse toGetResponse(User user);
    List<UserGetResponse> toGetAllResponse(List<User> users);

    UserUpdateResponse toUpdateResponse(User user);
    List<UserListResponse> toListResponse(List<User> users);
}
