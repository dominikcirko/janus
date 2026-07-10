package io.github.janus.identity.adapter.in.web.mapper;

import io.github.janus.identity.adapter.in.web.dto.user.request.UserCreateRequest;
import io.github.janus.identity.adapter.in.web.dto.user.request.UserUpdateRequest;
import io.github.janus.identity.adapter.in.web.dto.user.response.UserGetResponse;
import io.github.janus.identity.adapter.in.web.dto.user.response.UserListResponse;
import io.github.janus.identity.adapter.in.web.dto.user.response.UserUpdateResponse;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserWebMapper {

    UserCreateCommand createReqToCommand(UserCreateRequest request, UUID tenantId);
    UserUpdateCommand updateReqToCommand(UserUpdateRequest request, UUID tenantId);

    UserGetResponse toGetResponse(User user);
    List<UserGetResponse> toGetAllResponse(List<User> users);

    UserUpdateResponse toUpdateResponse(User user);
    List<UserListResponse> toListResponse(List<User> users);


}
