package io.github.janus.identity.application.service;

import io.github.janus.identity.application.port.in.usecase.user.CreateUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.DeleteUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.GetUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.UpdateUserUseCase;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserDeleteCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByUsernameQuery;
import io.github.janus.identity.domain.model.User;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements CreateUserUseCase,
        DeleteUserUseCase, GetUserUseCase, UpdateUserUseCase {

    private final Argon2PasswordEncoder encoder;
    private final CreateUserUseCase createUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserService(Argon2PasswordEncoder encoder, CreateUserUseCase createUserUseCase,
                       DeleteUserUseCase deleteUserUseCase,
                       GetUserUseCase getUserUseCase,
                       UpdateUserUseCase updateUserUseCase) {
        this.encoder = encoder;
        this.createUserUseCase = createUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    @Override
    public void create(UserCreateCommand command) {

        createUserUseCase.create(command);
    }

    @Override
    public void delete(UserDeleteCommand command) {

    }

    @Override
    public User getbyId(UserGetByIdQuery query) {
        return null;
    }

    @Override
    public User getbyUsername(UserGetByUsernameQuery query) {
        return null;
    }

    @Override
    public User getbyEmail(UserGetByEmailQuery query) {
        return null;
    }

    @Override
    public User update(UserUpdateCommand command) {
        return null;
    }
}
