package io.github.janus.identity.application.port.in;

import io.github.janus.identity.application.port.in.command.UserDeleteCommand;

public interface DeleteUserUseCase {

    void delete(UserDeleteCommand command);

}
