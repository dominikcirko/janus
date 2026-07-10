package io.github.janus.identity.credential.adapter.out.persistence;

import io.github.janus.identity.credential.domain.type.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialJpaRepository extends JpaRepository<CredentialEntity, UUID> {

    List<CredentialEntity> findAllByUserId(UUID userId);
    Optional<CredentialEntity> findByIdAndUserId(UUID id, UUID userId);
    Optional<CredentialEntity> findByUserIdAndType(UUID userId, CredentialType type);
}
