package io.github.janus.identity.user.application.port.in.usecase;

import io.github.janus.identity.user.application.port.in.query.UserGetAllQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByEmailQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByIdQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByUsernameQuery;
import io.github.janus.identity.user.domain.model.User;

import java.util.List;

public interface GetUserUseCase {

    User getbyId(UserGetByIdQuery query);
    User getbyUsername(UserGetByUsernameQuery query);
    User getbyEmail(UserGetByEmailQuery query);
    List<User> getAll(UserGetAllQuery query);

}
