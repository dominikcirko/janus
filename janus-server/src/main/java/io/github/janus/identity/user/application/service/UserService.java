package io.github.janus.identity.user.application.service;

import io.github.janus.identity.user.application.mapper.UserApplicationMapper;
import io.github.janus.identity.credential.application.port.in.command.CredentialCreateCommand;
import io.github.janus.identity.user.application.port.in.query.UserGetAllQuery;
import io.github.janus.identity.credential.application.port.in.usecase.CreateCredentialUseCase;
import io.github.janus.identity.user.application.port.in.usecase.CreateUserUseCase;
import io.github.janus.identity.user.application.port.in.usecase.DeleteUserUseCase;
import io.github.janus.identity.user.application.port.in.usecase.GetUserUseCase;
import io.github.janus.identity.user.application.port.in.usecase.UpdateUserUseCase;
import io.github.janus.identity.user.application.port.in.command.UserCreateCommand;
import io.github.janus.identity.user.application.port.in.command.UserDeleteCommand;
import io.github.janus.identity.user.application.port.in.command.UserUpdateCommand;
import io.github.janus.identity.user.application.port.in.query.UserGetByEmailQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByIdQuery;
import io.github.janus.identity.user.application.port.in.query.UserGetByUsernameQuery;
import io.github.janus.identity.user.application.port.out.UserRepository;
import io.github.janus.identity.user.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements CreateUserUseCase,
        DeleteUserUseCase, GetUserUseCase, UpdateUserUseCase {


    private final UserApplicationMapper mapper;
    private final UserRepository userRepository;
    private final CreateCredentialUseCase credentialUseCase;

    public UserService(UserApplicationMapper mapper,
                       UserRepository userRepository,
                       CreateCredentialUseCase credentialUseCase) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.credentialUseCase = credentialUseCase;
    }

    @Override
    public void create(UserCreateCommand command) {
        var user = userRepository.save(mapper.toDomain(command));
        credentialUseCase.create(
                new CredentialCreateCommand(
                        command.credentialType(),
                        command.credentialValue(),
                        user.getId()
                )
        );
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
