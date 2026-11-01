package janus.identity.user.application.mapper;

import janus.identity.user.domain.type.UserStatus;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testWhenUserInputValid_thenReturnPendingUser() {
        var input = UserTestDataFactory.createValidInput();

        var result = mapper.toUser(input, input.email());

        assertNull(result.getId());
        assertEquals(UserStatus.PENDING_ACTIVATION, result.getStatus());
        assertEquals(input.email(), result.getEmail());
        assertFalse(result.isEmailVerified());
        assertEquals(input.displayName(), result.getDisplayName());
        assertEquals(input.userMetadata(), result.getUserMetadata());
        assertEquals(0, result.getSystemMetadata().size());
    }

    @Test
    void testWhenUserUpdateValid_thenReturnUpdatedUser() {
        var user = UserTestDataFactory.createActiveUser();
        var input = UserTestDataFactory.createValidUpdateInput();

        var result = mapper.toUpdatedUser(user, input);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getStatus(), result.getStatus());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.isEmailVerified(), result.isEmailVerified());
        assertEquals(input.displayName(), result.getDisplayName());
        assertEquals(input.userMetadata(), result.getUserMetadata());
        assertEquals(user.getSystemMetadata(), result.getSystemMetadata());
    }

    @Test
    void testWhenUserStatusChanges_thenReturnUserWithUpdatedStatus() {
        var user = UserTestDataFactory.createActiveUser();

        var result = mapper.withUpdatedStatus(user, UserStatus.SUSPENDED);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getUserMetadata(), result.getUserMetadata());
        assertEquals(UserStatus.SUSPENDED, result.getStatus());
    }

    @Test
    void testWhenUserValid_thenReturnOutput() {
        var user = UserTestDataFactory.createActiveUser();
        var expected = UserTestDataFactory.createActiveOutput();

        var result = mapper.toOutput(user);

        assertEquals(expected, result);
    }
}
