package io.github.janus.identity.adapter.in.web.mapper;

import io.github.janus.identity.adapter.in.web.dto.UserCreateRequest;
import io.github.janus.identity.adapter.in.web.dto.UserGetRequest;
import io.github.janus.identity.adapter.in.web.dto.UserResponse;
import io.github.janus.identity.adapter.in.web.dto.UserUpdateRequest;
import io.github.janus.identity.application.port.in.command.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.UserUpdateCommand;
import io.github.janus.identity.application.port.in.query.UserGetByIdQuery;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserWebMapper {

    //domain to/from req/resp
    UserCreateCommand createReqToCommand(UserCreateRequest request);
    UserUpdateCommand updateReqToCommand(UserUpdateRequest request);
    UserGetByIdQuery updateReqToCommand(UserGetRequest request);

    UserResponse toCreateResponse(User user);

}
