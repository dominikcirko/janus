package janus.identity.user.application.port.usecase;

import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.application.port.usecase.model.UserUpdateInput;
import janus.identity.user.domain.type.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {

    UserOutput createUser(UserCreateInput input);
    UserOutput getUser(UUID userId);
    List<UserOutput> getUsers();
    UserOutput updateUser(UUID userId, UserUpdateInput input);
    UserOutput changeUserStatus(UUID userId, UserStatus status);
    void deleteUser(UUID userId);
}
