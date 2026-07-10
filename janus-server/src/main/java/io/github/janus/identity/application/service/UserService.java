package io.github.janus.identity.application.service;

import com.zaxxer.hikari.util.Credentials;
import io.github.janus.identity.application.mapper.UserApplicationMapper;
import io.github.janus.identity.application.port.in.query.user.UserGetAllQuery;
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
import io.github.janus.identity.domain.model.Credential;
import io.github.janus.identity.domain.model.User;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements CreateUserUseCase,
        DeleteUserUseCase, GetUserUseCase, UpdateUserUseCase {


    private final Argon2PasswordEncoder encoder;
    private final UserApplicationMapper mapper;
    private final UserRepository userRepository;

    public UserService(Argon2PasswordEncoder encoder,
                       UserApplicationMapper mapper,
                       UserRepository userRepository) {
        this.encoder = encoder;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public void create(UserCreateCommand command) {
        var hashedPassword = encoder.encode(command.password());
        var domainUser = mapper.toDomain(command);
        userRepository.save(domainUser);
    }

    @Override
    public void delete(UserDeleteCommand command) {
        var domainUser = mapper.toDomain(command);
        userRepository.delete(domainUser.getId(), domainUser.getTenant().getId());
    }

    @Override
    public User getbyId(UserGetByIdQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findById(domainUser.getId(), domainUser.getTenant().getId());
    }

    @Override
    public User getbyUsername(UserGetByUsernameQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findByUsername(domainUser.getUsername(), domainUser.getTenant().getId());
    }

    @Override
    public User getbyEmail(UserGetByEmailQuery query) {
        var domainUser = mapper.toDomain(query);
        return userRepository.findByEmail(domainUser.getEmail(), domainUser.getTenant().getId());
    }

    @Override
    public List<User> getAll(UserGetAllQuery query) {
        return userRepository.findAll(query.tenantId());
    }

    @Override
    public User update(UserUpdateCommand command) {
       var domainUser  = mapper.toDomain(command);
       return userRepository.update(domainUser);
    }
}
