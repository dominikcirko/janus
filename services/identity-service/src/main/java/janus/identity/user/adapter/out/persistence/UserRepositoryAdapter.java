package janus.identity.user.adapter.out.persistence;

import janus.identity.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import janus.identity.user.application.port.out.UserRepository;
import janus.identity.user.application.port.usecase.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository repo;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = repo.save(entity);

        return mapper.toUser(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email)
                .map(mapper::toUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toUser)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
