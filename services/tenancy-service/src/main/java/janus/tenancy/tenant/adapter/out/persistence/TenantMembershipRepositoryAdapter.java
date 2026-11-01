package janus.tenancy.tenant.adapter.out.persistence;

import janus.tenancy.tenant.adapter.out.persistence.mapper.TenantMembershipPersistenceMapper;
import janus.tenancy.tenant.application.port.out.TenantMembershipRepository;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TenantMembershipRepositoryAdapter implements TenantMembershipRepository {

    private final JpaTenantMembershipRepository repo;
    private final JpaTenantRepository tenantRepo;
    private final TenantMembershipPersistenceMapper mapper;

    @Override
    public TenantMembership save(TenantMembership membership) {
        var tenantEntity = tenantRepo.getReferenceById(membership.getTenantId());
        var entity = mapper.toEntity(membership, tenantEntity);
        var savedEntity = repo.save(entity);

        return mapper.toMembership(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantMembership> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantMembership> findAllByTenantId(UUID tenantId) {
        return repo.findAllByTenantId(tenantId)
                .stream()
                .map(mapper::toMembership)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTenantIdAndUserId(UUID tenantId, UUID userId) {
        return repo.existsByTenantIdAndUserId(tenantId, userId);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
