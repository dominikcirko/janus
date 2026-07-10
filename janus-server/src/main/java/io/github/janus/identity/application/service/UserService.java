package io.github.janus.identity.application.service;

import io.github.janus.identity.application.mapper.UserApplicationMapper;
import io.github.janus.identity.application.port.in.command.credential.CredentialCreateCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetAllQuery;
import io.github.janus.identity.application.port.in.usecase.credential.CreateCredentialUseCase;
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
import io.github.janus.identity.application.port.out.UserRepository;
import io.github.janus.identity.domain.model.User;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements CreateUserUseCase,
        DeleteUserUseCase, GetUserUseCase, UpdateUserUseCase {


    private final Argon2PasswordEncoder encoder;
    private final UserApplicationMapper mapper;
    private final UserRepository userRepository;
    private final CreateCredentialUseCase credentialUseCase;

    public UserService(Argon2PasswordEncoder encoder,
                       UserApplicationMapper mapper,
                       UserRepository userRepository, CreateCredentialUseCase credentialUseCase) {
        this.encoder = encoder;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.credentialUseCase = credentialUseCase;
    }

    @Override
    public User create(UserCreateCommand command) {
        var user = userRepository.save(mapper.toDomain(command));
        credentialUseCase.create(
                new CredentialCreateCommand(
                        command.credentialType(),
                        command.credentialValue(),
                        user.getId()
                )
        );
        return userRepository.findById(user.getId());
    }

    @Override
    public void delete(UserDeleteCommand command) {
        var domainUser = mapper.toDomain(command);
        userRepository.delete(domainUser.getId());
    }

    @Override
    public User getbyId(UserGetByIdQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findById(domainUser.getId());
    }

    @Override
    public User getbyUsername(UserGetByUsernameQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findByUsername(domainUser.getUsername());
    }

    @Override
    public User getbyEmail(UserGetByEmailQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findByEmail(domainUser.getEmail());
    }

    @Override
    public List<User> getAll(UserGetAllQuery query) {
        return userRepository.findAll();
    }

    //todo: credential updates go in their own separate flow, not here
    @Override
    public User update(UserUpdateCommand command) {
       var domainUser  = mapper.toDomain(command);
       return userRepository.update(domainUser);
    }
}
