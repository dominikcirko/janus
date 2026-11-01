package janus.tenancy.tenant.adapter.out.persistence;

import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationRepository;
import janus.tenancy.tenant.adapter.out.persistence.mapper.TenantPersistenceMapper;
import janus.tenancy.tenant.application.port.out.TenantRepository;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryAdapter implements TenantRepository {

    private final JpaTenantRepository repo;
    private final JpaOrganizationRepository organizationRepo;
    private final TenantPersistenceMapper mapper;

    @Override
    public Tenant save(Tenant tenant) {
        var organizationEntity = organizationRepo.getReferenceById(tenant.getOrganizationId());
        var entity = mapper.toEntity(tenant, organizationEntity);
        var savedEntity = repo.save(entity);

        return mapper.toTenant(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findAllByOrganizationId(UUID organizationId) {
        return repo.findAllByOrganizationId(organizationId)
                .stream()
                .map(mapper::toTenant)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrganizationIdAndSlug(UUID organizationId, String slug) {
        return repo.existsByOrganizationIdAndSlug(organizationId, slug);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIssuer(String issuer) {
        return repo.existsByIssuer(issuer);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
