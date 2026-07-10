package io.github.janus.identity.user.application.port.out;

import io.github.janus.identity.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User findById(UUID id);
    User findByEmail(String email);
    User findByUsername(String username);

    User save(User user);
    User update(User user);
    void delete(UUID id);
    List<User> findAll();
}
