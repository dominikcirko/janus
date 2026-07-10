package io.github.janus.identity.application.port.in.usecase.user;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.domain.model.User;

public interface CreateUserUseCase {

    User create(UserCreateCommand userCommand);

}
