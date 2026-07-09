package io.github.janus.identity.adapter.in.web.mapper;

import io.github.janus.identity.adapter.in.web.dto.UserCreateRequest;
import io.github.janus.identity.adapter.in.web.dto.UserResponse;
import io.github.janus.identity.adapter.in.web.dto.UserUpdateRequest;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserWebMapper {

    //domain to/from req/resp
    User createReqToDomain(UserCreateRequest request);
    User updateReqToDomain(UserUpdateRequest request);
    UserResponse toResponse(User user);

}
