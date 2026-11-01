package janus.identity.user.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.identity.user.application.mapper.UserMapper;
import janus.identity.user.application.port.out.UserRepository;
import janus.identity.user.application.port.usecase.PasswordCredentialUseCase;
import janus.identity.user.application.port.usecase.UserUseCase;
import janus.identity.user.application.port.usecase.model.User;
import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.application.port.usecase.model.UserUpdateInput;
import janus.identity.user.domain.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private static final String USER = "User";

    private final UserRepository repository;
    private final PasswordCredentialUseCase passwordCredentialUseCase;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserOutput createUser(UserCreateInput input) {
        validateCreateInput(input);

        var normalizedEmail = normalizeEmail(input.email());
        if (repository.existsByEmail(normalizedEmail)) {
            throw new ResourceAlreadyExistsException(USER, "email", normalizedEmail);
        }

        var user = mapper.toUser(input, normalizedEmail);
        var savedUser = repository.save(user);
        passwordCredentialUseCase.createPasswordCredential(savedUser.getId(), input.password(), false);

        return mapper.toOutput(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserOutput getUser(UUID userId) {
        return mapper.toOutput(findUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserOutput> getUsers() {
        return repository.findAll()
                .stream()
                .map(mapper::toOutput)
                .toList();
    }

    @Override
    @Transactional
    public UserOutput updateUser(UUID userId, UserUpdateInput input) {
        validateUpdateInput(input);

        var user = findUser(userId);
        var updatedUser = mapper.toUpdatedUser(user, input);
        var savedUser = repository.save(updatedUser);

        return mapper.toOutput(savedUser);
    }

    @Override
    @Transactional
    public UserOutput changeUserStatus(UUID userId, UserStatus status) {
        if (status == null) {
            throw new InvalidRequestException("User status must not be null");
        }

        var user = findUser(userId);
        var updatedUser = mapper.withUpdatedStatus(user, status);
        var savedUser = repository.save(updatedUser);

        return mapper.toOutput(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        findUser(userId);
        passwordCredentialUseCase.deletePasswordCredential(userId);
        repository.deleteById(userId);
    }

    private User findUser(UUID userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER, "id", userId));
    }

    private void validateCreateInput(UserCreateInput input) {
        if (input == null) {
            throw new InvalidRequestException("User input must not be null");
        }

        if (input.email() == null || input.email().isBlank()) {
            throw new InvalidRequestException("User email must not be null or blank");
        }

        if (input.password() == null || input.password().isBlank()) {
            throw new InvalidRequestException("User password must not be null or blank");
        }

        validateUserMetadata(input.userMetadata());
    }

    private void validateUpdateInput(UserUpdateInput input) {
        if (input == null) {
            throw new InvalidRequestException("User update input must not be null");
        }

        validateUserMetadata(input.userMetadata());
    }

    private void validateUserMetadata(Map<String, Object> userMetadata) {
        if (userMetadata == null) {
            throw new InvalidRequestException("User metadata must not be null");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
