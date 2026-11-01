package janus.identity.user.adapter.out.persistence;

import janus.identity.user.adapter.out.persistence.entity.PasswordCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPasswordCredentialRepository extends JpaRepository<PasswordCredentialEntity, UUID> {
}
