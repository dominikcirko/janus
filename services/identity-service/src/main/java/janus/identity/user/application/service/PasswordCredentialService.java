package janus.identity.user.application.service;

import janus.common.exceptionhandler.exception.ForbiddenException;
import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.identity.user.application.port.out.PasswordCredentialRepository;
import janus.identity.user.application.port.out.UserRepository;
import janus.identity.user.application.port.usecase.PasswordCredentialUseCase;
import janus.identity.user.application.port.usecase.model.PasswordCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordCredentialService implements PasswordCredentialUseCase {

    private static final String USER = "User";
    private static final String PASSWORD_CREDENTIAL = "Password credential";

    private final PasswordCredentialRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createPasswordCredential(UUID userId, String rawPassword, boolean mustChangePassword) {
        validateUserId(userId);
        validatePassword(rawPassword, "Password");
        validateUserExists(userId);

        if (repository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException(PASSWORD_CREDENTIAL, "userId", userId);
        }

        var credential = PasswordCredential.builder()
                .userId(userId)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .passwordChangedAt(Instant.now())
                .mustChangePassword(mustChangePassword)
                .build();

        repository.save(credential);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyPassword(UUID userId, String rawPassword) {
        validateUserId(userId);
        validatePassword(rawPassword, "Password");

        if (userRepository.findById(userId).isEmpty()) {
            return false;
        }

        return repository.findByUserId(userId)
                .map(credential -> passwordEncoder.matches(rawPassword, credential.getPasswordHash()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        validateUserId(userId);
        validatePassword(currentPassword, "Current password");
        validatePassword(newPassword, "New password");
        validateUserExists(userId);

        var credential = findPasswordCredential(userId);
        if (!passwordEncoder.matches(currentPassword, credential.getPasswordHash())) {
            throw new ForbiddenException("Current password is invalid");
        }

        var updatedCredential = PasswordCredential.builder()
                .userId(userId)
                .passwordHash(passwordEncoder.encode(newPassword))
                .passwordChangedAt(Instant.now())
                .mustChangePassword(false)
                .build();

        repository.save(updatedCredential);
    }

    @Override
    @Transactional
    public void deletePasswordCredential(UUID userId) {
        validateUserId(userId);
        validateUserExists(userId);
        repository.deleteByUserId(userId);
    }

    private PasswordCredential findPasswordCredential(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(PASSWORD_CREDENTIAL, "userId", userId));
    }

    private void validateUserExists(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER, "id", userId));
    }

    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidRequestException("User id must not be null");
        }
    }

    private void validatePassword(String password, String fieldName) {
        if (password == null || password.isBlank()) {
            throw new InvalidRequestException(fieldName + " must not be null or blank");
        }
    }
}
