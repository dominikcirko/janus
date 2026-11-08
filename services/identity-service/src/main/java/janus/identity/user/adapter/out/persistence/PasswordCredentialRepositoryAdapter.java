package janus.identity.user.adapter.out.persistence;

import jakarta.persistence.EntityManager;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.identity.user.adapter.out.persistence.entity.UserEntity;
import janus.identity.user.adapter.out.persistence.mapper.PasswordCredentialPersistenceMapper;
import janus.identity.user.application.port.out.PasswordCredentialRepository;
import janus.identity.user.application.port.usecase.model.PasswordCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PasswordCredentialRepositoryAdapter implements PasswordCredentialRepository {

    private final JpaPasswordCredentialRepository repo;
    private final JpaUserRepository userRepo;
    private final PasswordCredentialPersistenceMapper mapper;
    private final EntityManager entityManager;

    /**
     * Uses persist because @MapsId assigns the user's ID before the credential row exists;
     * save would treat creation as an update instead of an insert.
     */
    @Override
    @Transactional
    public void create(PasswordCredential credential) {
        var userEntity = findUser(credential.getUserId());
        var entity = mapper.toEntity(credential, userEntity);
        entityManager.persist(entity);
    }

    @Override
    @Transactional
    public void update(PasswordCredential credential) {
        var userEntity = findUser(credential.getUserId());
        var entity = repo.findById(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Password credential", "userId", credential.getUserId()));
        entity.updatePassword(
                credential.getPasswordHash(),
                credential.getPasswordChangedAt(),
                credential.isMustChangePassword()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordCredential> findByUserId(UUID userId) {
        if (userRepo.findById(userId).isEmpty()) {
            return Optional.empty();
        }
        return repo.findById(userId)
                .map(mapper::toCredential);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        if (userRepo.findById(userId).isEmpty()) {
            return false;
        }
        return repo.existsById(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        if (userRepo.findById(userId).isEmpty()) {
            return;
        }
        repo.deleteById(userId);
    }

    private UserEntity findUser(UUID userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
