package io.github.janus.identity.application.port.in.usecase.user;

import io.github.janus.identity.application.port.in.command.user.UserDeleteCommand;

public interface DeleteUserUseCase {

    void delete(UserDeleteCommand command);

}
