package io.github.janus.identity.user.application.port.in.usecase;

import io.github.janus.identity.user.application.port.in.command.UserDeleteCommand;

public interface DeleteUserUseCase {

    void delete(UserDeleteCommand command);

}
