package janus.identity.user.testdata;

import janus.identity.user.adapter.in.web.dto.PasswordChangeRequest;
import janus.identity.user.adapter.in.web.dto.PasswordVerificationRequest;
import janus.identity.user.adapter.in.web.dto.UserRequest;
import janus.identity.user.adapter.in.web.dto.UserResponse;
import janus.identity.user.adapter.in.web.dto.UserUpdateRequest;
import janus.identity.user.application.port.usecase.model.PasswordCredential;
import janus.identity.user.application.port.usecase.model.User;
import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.application.port.usecase.model.UserUpdateInput;
import janus.identity.user.domain.type.UserStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class UserTestDataFactory {

    public static final UUID TEST_USER_ID = UUID.fromString("bf9ceae3-8e1a-485b-98ca-1f611f6faaf4");
    public static final String TEST_EMAIL = "user@example.com";
    public static final String TEST_PASSWORD = "test-password";
    public static final String TEST_PASSWORD_HASH = "{bcrypt}test-password-hash";
    public static final String TEST_NEW_PASSWORD = "new-test-password";
    public static final String TEST_NEW_PASSWORD_HASH = "{bcrypt}new-test-password-hash";

    private static final String TEST_DISPLAY_NAME = "Test User";
    private static final String TEST_GIVEN_NAME = "Test";
    private static final String TEST_FAMILY_NAME = "User";
    private static final String TEST_PICTURE_URI = "https://example.com/user.png";
    private static final Map<String, Object> TEST_USER_METADATA = Map.of("department", "engineering");
    private static final Map<String, Object> TEST_SYSTEM_METADATA = Map.of("source", "janus");

    private UserTestDataFactory() {
    }

    public static UserCreateInput createValidInput() {
        return new UserCreateInput(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_DISPLAY_NAME,
                TEST_GIVEN_NAME,
                TEST_FAMILY_NAME,
                TEST_PICTURE_URI,
                TEST_USER_METADATA
        );
    }

    public static UserCreateInput createInputWithEmail(String email) {
        var input = createValidInput();

        return new UserCreateInput(
                email,
                input.password(),
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                input.userMetadata()
        );
    }

    public static UserCreateInput createInputWithPassword(String password) {
        var input = createValidInput();

        return new UserCreateInput(
                input.email(),
                password,
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                input.userMetadata()
        );
    }

    public static UserCreateInput createInputWithMetadata(Map<String, Object> metadata) {
        var input = createValidInput();

        return new UserCreateInput(
                input.email(),
                input.password(),
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                metadata
        );
    }

    public static UserUpdateInput createValidUpdateInput() {
        return new UserUpdateInput(
                "Updated User",
                "Updated",
                "Name",
                "https://example.com/updated-user.png",
                Map.of("department", "security")
        );
    }

    public static UserUpdateInput createUpdateInputWithMetadata(Map<String, Object> metadata) {
        var input = createValidUpdateInput();

        return new UserUpdateInput(
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                metadata
        );
    }

    public static User createPendingUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .status(UserStatus.PENDING_ACTIVATION)
                .email(TEST_EMAIL)
                .emailVerified(false)
                .displayName(TEST_DISPLAY_NAME)
                .givenName(TEST_GIVEN_NAME)
                .familyName(TEST_FAMILY_NAME)
                .pictureUri(TEST_PICTURE_URI)
                .userMetadata(TEST_USER_METADATA)
                .systemMetadata(TEST_SYSTEM_METADATA)
                .build();
    }

    public static User createActiveUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .status(UserStatus.ACTIVE)
                .email(TEST_EMAIL)
                .emailVerified(true)
                .displayName(TEST_DISPLAY_NAME)
                .givenName(TEST_GIVEN_NAME)
                .familyName(TEST_FAMILY_NAME)
                .pictureUri(TEST_PICTURE_URI)
                .userMetadata(TEST_USER_METADATA)
                .systemMetadata(TEST_SYSTEM_METADATA)
                .build();
    }

    public static UserOutput createActiveOutput() {
        var user = createActiveUser();

        return new UserOutput(
                user.getId(),
                user.getStatus(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getDisplayName(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getPictureUri(),
                user.getUserMetadata(),
                user.getSystemMetadata()
        );
    }

    public static UserOutput createSuspendedOutput() {
        var output = createActiveOutput();

        return new UserOutput(
                output.id(),
                UserStatus.SUSPENDED,
                output.email(),
                output.emailVerified(),
                output.displayName(),
                output.givenName(),
                output.familyName(),
                output.pictureUri(),
                output.userMetadata(),
                output.systemMetadata()
        );
    }

    public static UserRequest createValidRequest() {
        var input = createValidInput();

        return new UserRequest(
                input.email(),
                input.password(),
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                input.userMetadata()
        );
    }

    public static UserUpdateRequest createValidUpdateRequest() {
        var input = createValidUpdateInput();

        return new UserUpdateRequest(
                input.displayName(),
                input.givenName(),
                input.familyName(),
                input.pictureUri(),
                input.userMetadata()
        );
    }

    public static UserResponse createActiveResponse() {
        var output = createActiveOutput();

        return new UserResponse(
                output.id(),
                output.status(),
                output.email(),
                output.emailVerified(),
                output.displayName(),
                output.givenName(),
                output.familyName(),
                output.pictureUri(),
                output.userMetadata(),
                output.systemMetadata()
        );
    }

    public static UserResponse createSuspendedResponse() {
        var output = createSuspendedOutput();

        return new UserResponse(
                output.id(),
                output.status(),
                output.email(),
                output.emailVerified(),
                output.displayName(),
                output.givenName(),
                output.familyName(),
                output.pictureUri(),
                output.userMetadata(),
                output.systemMetadata()
        );
    }

    public static PasswordCredential createPasswordCredential() {
        return PasswordCredential.builder()
                .userId(TEST_USER_ID)
                .passwordHash(TEST_PASSWORD_HASH)
                .passwordChangedAt(Instant.parse("2026-11-01T12:00:00Z"))
                .mustChangePassword(true)
                .build();
    }

    public static PasswordVerificationRequest createPasswordVerificationRequest() {
        return new PasswordVerificationRequest(TEST_PASSWORD);
    }

    public static PasswordChangeRequest createPasswordChangeRequest() {
        return new PasswordChangeRequest(TEST_PASSWORD, TEST_NEW_PASSWORD);
    }
}
