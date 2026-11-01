package janus.tenancy.organization.adapter.out.persistence;

import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaOrganizationMembershipRepository extends JpaRepository<OrganizationMembershipEntity, UUID> {

    List<OrganizationMembershipEntity> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}
