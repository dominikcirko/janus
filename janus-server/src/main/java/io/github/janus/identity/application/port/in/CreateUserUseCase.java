package io.github.janus.identity.application.port.in;

import io.github.janus.identity.application.port.in.command.UserCreateCommand;

public interface CreateUserUseCase {

    void create(UserCreateCommand command);

}
