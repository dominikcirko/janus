package io.github.janus.identity.user.application.mapper;

import io.github.janus.identity.user.application.port.in.command.UserCreateCommand;
import io.github.janus.identity.user.application.port.in.command.UserDeleteCommand;
import io.github.janus.identity.user.application.port.in.command.UserUpdateCommand;
import io.github.janus.identity.user.application.port.in.query.UserGetAllQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByEmailQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByIdQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByUsernameQuery;
import io.github.janus.identity.user.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserApplicationMapper {

    User toDomain(UserCreateCommand command);

    User toDomain(UserUpdateCommand command);

    User toDomain(UserDeleteCommand command);

    User toDomain(UserGetByIdQuery query);
    User toDomain(UserGetByUsernameQuery query);
    User toDomain(UserGetByEmailQuery query);

    List<User> toListDomain(List<UserGetAllQuery> query);

}
