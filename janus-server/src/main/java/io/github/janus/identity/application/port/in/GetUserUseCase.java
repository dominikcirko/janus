package io.github.janus.identity.application.port.in;

import io.github.janus.identity.application.port.in.query.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.UserGetByUsernameQuery;
import io.github.janus.identity.domain.model.User;

public interface GetUserUseCase {

    User getbyId(UserGetByIdQuery query);
    User getbyUsername(UserGetByUsernameQuery query);
    User getbyEmail(UserGetByEmailQuery query);

}
