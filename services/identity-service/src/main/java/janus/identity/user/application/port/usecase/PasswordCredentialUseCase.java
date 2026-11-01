package janus.identity.user.application.port.usecase;

import java.util.UUID;

public interface PasswordCredentialUseCase {

    void createPasswordCredential(UUID userId, String rawPassword, boolean mustChangePassword);
    boolean verifyPassword(UUID userId, String rawPassword);
    void changePassword(UUID userId, String currentPassword, String newPassword);
    void deletePasswordCredential(UUID userId);
}
