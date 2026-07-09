package io.github.janus.identity.application.port.out;

import io.github.janus.identity.domain.model.User;

import java.util.UUID;

public interface UserRepository {

    User findById(UUID userId);

}
