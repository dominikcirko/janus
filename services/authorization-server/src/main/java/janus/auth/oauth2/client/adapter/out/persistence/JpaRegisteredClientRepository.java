package janus.auth.oauth2.client.adapter.out.persistence;

import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaRegisteredClientRepository extends JpaRepository<RegisteredClientEntity, UUID> {
    Optional<RegisteredClientEntity> findByClientId(String clientId);
}
