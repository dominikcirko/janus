package io.github.janus.identity.adapter.out.persistence;

import io.github.janus.identity.adapter.out.persistence.mapper.UserPersistenceMapper;
import io.github.janus.identity.adapter.out.persistence.repository.UserJpaRepository;
import io.github.janus.identity.application.port.out.UserRepository;
import io.github.janus.identity.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User findById(UUID userId) {
        var user = repository.findById(userId);
        return mapper.toDomain(user);
    }
}
