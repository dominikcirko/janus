package janus.tenancy.tenant.adapter.out.persistence;

import janus.tenancy.tenant.adapter.out.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTenantRepository extends JpaRepository<TenantEntity, UUID> {

    List<TenantEntity> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndSlug(UUID organizationId, String slug);
    boolean existsByIssuer(String issuer);
}
