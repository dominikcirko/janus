package io.github.janus.identity.adapter.in.web.mapper;

import io.github.janus.identity.adapter.in.web.dto.UserCreateRequest;
import io.github.janus.identity.adapter.in.web.dto.UserGetRequest;
import io.github.janus.identity.adapter.in.web.dto.UserResponse;
import io.github.janus.identity.adapter.in.web.dto.UserUpdateRequest;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserWebMapper {

    UserCreateCommand createReqToCommand(UserCreateRequest request);
    UserUpdateCommand updateReqToCommand(UserUpdateRequest request);
    UserGetByIdQuery updateReqToCommand(UserGetRequest request);

    UserResponse toCreateResponse(User user);

}
