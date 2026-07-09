package io.github.janus.identity.adapter.out.persistence;

import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;
import io.github.janus.identity.adapter.out.persistence.mapper.UserPeristenceMapper;
import io.github.janus.identity.adapter.out.persistence.repository.UserJpaRepository;
import io.github.janus.identity.application.port.out.UserRepository;

import java.util.UUID;

public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPeristenceMapper userPeristenceMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPeristenceMapper userPeristenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPeristenceMapper = userPeristenceMapper;
    }

    @Override
    public UserEntity findById(UUID userId) {
        var user = userJpaRepository.findById(userId);
    }
}
