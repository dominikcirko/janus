package janus.tenancy.organization.application.port.out;

import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMembershipRepository {

    OrganizationMembership save(OrganizationMembership membership);
    Optional<OrganizationMembership> findById(UUID id);
    List<OrganizationMembership> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    void deleteById(UUID id);
}
