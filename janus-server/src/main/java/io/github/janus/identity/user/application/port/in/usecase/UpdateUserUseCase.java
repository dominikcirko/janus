package io.github.janus.identity.user.application.port.in.usecase;

import io.github.janus.identity.user.application.port.in.command.UserUpdateCommand;
import io.github.janus.identity.user.domain.model.User;

public interface UpdateUserUseCase {
    User update(UserUpdateCommand command);
}
