package io.github.janus.identity.application.port.in.usecase.user;

import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;

public interface CreateUserUseCase {

    void create(UserCreateCommand command);

}
