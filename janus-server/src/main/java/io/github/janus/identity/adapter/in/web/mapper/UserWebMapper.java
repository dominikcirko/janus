package io.github.janus.identity.adapter.in.web.mapper;

import io.github.janus.identity.adapter.in.web.dto.user.*;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserWebMapper {

    UserCreateCommand createReqToCommand(UserCreateRequest request);
    UserUpdateCommand updateReqToCommand(UserUpdateRequest request);
    UserGetByIdQuery updateReqToCommand(UserGetByIdRequest request);

    UserGetResponse toGetResponse(User user);
    List<UserGetResponse> toGetAllResponse(List<User> users);

    UserUpdateResponse toUpdateResponse(User user);
    List<UserListResponse> toListResponse(List<User> users);


}
