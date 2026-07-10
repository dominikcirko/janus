package io.github.janus.identity.application.port.in.usecase.user;

import io.github.janus.identity.application.port.in.query.user.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByUsernameQuery;
import io.github.janus.identity.domain.model.User;

public interface GetUserUseCase {

    User getbyId(UserGetByIdQuery query);
    User getbyUsername(UserGetByUsernameQuery query);
    User getbyEmail(UserGetByEmailQuery query);

}
