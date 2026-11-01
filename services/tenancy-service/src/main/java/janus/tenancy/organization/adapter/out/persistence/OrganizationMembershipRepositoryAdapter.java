package janus.tenancy.organization.adapter.out.persistence;

import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationMembershipPersistenceMapper;
import janus.tenancy.organization.application.port.out.OrganizationMembershipRepository;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrganizationMembershipRepositoryAdapter implements OrganizationMembershipRepository {

    private final JpaOrganizationMembershipRepository repo;
    private final JpaOrganizationRepository organizationRepo;
    private final OrganizationMembershipPersistenceMapper mapper;

    @Override
    public OrganizationMembership save(OrganizationMembership membership) {
        var organizationEntity = organizationRepo.getReferenceById(membership.getOrganizationId());
        var entity = mapper.toEntity(membership, organizationEntity);
        var savedEntity = repo.save(entity);

        return mapper.toMembership(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationMembership> findById(UUID id) {
        return repo.findById(id)
                .map(mapper::toMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMembership> findAllByOrganizationId(UUID organizationId) {
        return repo.findAllByOrganizationId(organizationId)
                .stream()
                .map(mapper::toMembership)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return repo.existsByOrganizationIdAndUserId(organizationId, userId);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
