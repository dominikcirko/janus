package janus.tenancy.organization.adapter.out.persistence;

import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationPersistenceMapper;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final JpaOrganizationRepository repo;
    private final OrganizationPersistenceMapper mapper;

    @Override
    public Organization save(Organization organization) {
        var entity = mapper.toEntity(organization);
        var savedEntity = repo.save(entity);

        return mapper.toOrganization(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findBySlug(String slug) {
        return repo.findBySlug(slug)
                .map(mapper::toOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toOrganization)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return repo.existsBySlug(slug);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
