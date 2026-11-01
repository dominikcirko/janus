package janus.tenancy.organization.adapter.out.persistence;

import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {

    boolean existsBySlug(String slug);
}
