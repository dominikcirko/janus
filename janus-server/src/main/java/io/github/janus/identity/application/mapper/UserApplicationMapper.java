package io.github.janus.identity.application.mapper;

import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserDeleteCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetAllQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByUsernameQuery;
import io.github.janus.identity.domain.model.Credential;
import io.github.janus.identity.domain.model.Role;
import io.github.janus.identity.domain.model.User;
import io.github.janus.tenant.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserApplicationMapper {

    @Mapping(target = "credentials", ignore = true)
    User toDomain(UserCreateCommand command);

    User toDomain(UserUpdateCommand command);

    User toDomain(UserDeleteCommand command);

    User toDomain(UserGetByIdQuery query);
    User toDomain(UserGetByUsernameQuery query);
    User toDomain(UserGetByEmailQuery query);

    List<User> toListDomain(List<UserGetAllQuery> query);

}
