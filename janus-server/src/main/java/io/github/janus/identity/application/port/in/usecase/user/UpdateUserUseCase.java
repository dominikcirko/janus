package io.github.janus.identity.application.port.in.usecase.user;

import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.domain.model.User;

public interface UpdateUserUseCase {
    User update(UserUpdateCommand command);
}
