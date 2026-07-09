package io.github.janus.identity.application.port.out;

import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;

import java.util.UUID;

public interface UserRepository {

    UserEntity findById(UUID userId);

}
