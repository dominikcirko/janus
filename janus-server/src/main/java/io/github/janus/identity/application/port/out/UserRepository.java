package io.github.janus.identity.application.port.out;

import io.github.janus.identity.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User findById(UUID id, UUID tenantId);
    User findByEmail(String email, UUID tenantId);
    User findByUsername(String username, UUID tenantId);

    User save(User user);
    User update(User user);
    void delete(UUID id,  UUID tenantId);
    List<User> findAll(UUID tenantId);
}
