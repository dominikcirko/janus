package janus.identity.user.adapter.out.persistence;

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

    @Override
    public PasswordCredential save(PasswordCredential credential) {
        var userEntity = userRepo.getReferenceById(credential.getUserId());
        var entity = mapper.toEntity(credential, userEntity);
        var savedEntity = repo.save(entity);

        return mapper.toCredential(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordCredential> findByUserId(UUID userId) {
        return repo.findById(userId)
                .map(mapper::toCredential);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return repo.existsById(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repo.deleteById(userId);
    }
}
