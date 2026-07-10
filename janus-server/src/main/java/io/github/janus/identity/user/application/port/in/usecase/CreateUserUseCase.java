package io.github.janus.identity.user.application.port.in.usecase;
import io.github.janus.identity.user.application.port.in.command.UserCreateCommand;

public interface CreateUserUseCase {

    void create(UserCreateCommand userCommand);

}
